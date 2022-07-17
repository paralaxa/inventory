package sk.stopangin.saga.store;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;
import sk.stopangin.saga.common.EventSerde;
import sk.stopangin.saga.common.OrderedItem;
import sk.stopangin.saga.common.Serdes.OrderSerde;
import sk.stopangin.saga.common.Status;
import sk.stopangin.saga.domain.Order;
import sk.stopangin.saga.event.Event;
import sk.stopangin.saga.event.ItemAdded;
import sk.stopangin.saga.event.ItemRemoved;
import sk.stopangin.saga.event.OrderCanceled;
import sk.stopangin.saga.event.OrderCreated;


@Component
@RequiredArgsConstructor
public class KafkaOrderStore implements OrderStore {

  private static final EventSerde EVENT_SERDE = new EventSerde();

  private static final OrderSerde ORDER_SERDE = new OrderSerde();

  public static final String ORDER_STORE = "orderStore";

  private final StreamsBuilder streamsBuilder;
  private final StreamsBuilderFactoryBean factoryBean;

  @PostConstruct
  void buildPipeline() {
    streamsBuilder.stream("order",
            Consumed.with(Serdes.String(), EVENT_SERDE))
        .groupBy((s, event) -> event.getId(), Grouped.with(Serdes.String(), EVENT_SERDE))
        .reduce((event, eventNew) -> switch (eventNew) {

          case OrderCreated created ->
              new OrderFinalized(created.getId(), Status.OPEN, created.getOrderedItems());
          case OrderCanceled canceled -> {
            OrderFinalized orderFinalized = getOrderFinalized(event);
            orderFinalized.setStatus(Status.CANCELED);
            yield orderFinalized;
          }
          case ItemAdded added -> {
            OrderFinalized orderFinalized = getOrderFinalized(event);
            List<OrderedItem> orderedItems = orderFinalized.getOrderedItems();
            int indexOf = orderedItems.indexOf(added.getOrderedItem());
            if (indexOf > -1) {
              OrderedItem orderedItem = orderedItems.get(indexOf);
              orderedItem.setCount(orderedItem.getCount() + added.getOrderedItem().getCount());
            } else {
              orderedItems.add(added.getOrderedItem());
            }
            yield orderFinalized;
          }
          case ItemRemoved removed -> {
            OrderFinalized orderFinalized = getOrderFinalized(event);
            List<OrderedItem> orderedItems = orderFinalized.getOrderedItems();
            int indexOf = orderedItems.indexOf(removed.getOrderedItem());
            if (indexOf > -1) {
              OrderedItem orderedItem = orderedItems.get(indexOf);
              orderedItem.setCount(orderedItem.getCount() - removed.getOrderedItem().getCount());
              if (orderedItem.getCount() <= 0) {
                orderedItems.remove(indexOf);
              }
            }
            yield orderFinalized;
          }

          default -> throw new IllegalStateException("Unexpected value: " + eventNew);
        }, Materialized.as(ORDER_STORE));
  }

  private OrderFinalized getOrderFinalized(Event event) {
    if (event instanceof OrderFinalized) {
      return (OrderFinalized) event;
    }
    if (event instanceof ItemAdded) {
      return new OrderFinalized(event.getId(),
          ((ItemAdded) event).getOrderedItem());
    }
    return null;
  }

  @Override
  public Order getOrderById(String id) {
    KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
    ReadOnlyKeyValueStore<String, Event> orderStore = kafkaStreams.store(
        StoreQueryParameters.fromNameAndType(ORDER_STORE, QueryableStoreTypes.keyValueStore()));
    OrderFinalized event = (OrderFinalized) orderStore.get(id);
    Order result = new Order();
    result.setId(event.getId());
    result.setStatus(event.getStatus());
    result.setOrderedItems(event.getOrderedItems());
    return result;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class OrderFinalized implements Event {

    private String id;
    private Status status;
    private List<OrderedItem> orderedItems = new ArrayList<>();

    public OrderFinalized(String id, OrderedItem orderedItem) {
      this.orderedItems.add(orderedItem);
      this.id = id;
    }
  }
}
