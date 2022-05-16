package sk.stopangin.saga.store;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;
import sk.stopangin.saga.common.EventSerde;
import sk.stopangin.saga.common.Serdes.OrderSerde;
import sk.stopangin.saga.common.Status;
import sk.stopangin.saga.domain.Order;
import sk.stopangin.saga.event.Event;
import sk.stopangin.saga.event.OrderCreated;
import sk.stopangin.saga.event.OrderUpdated;


@Component
@RequiredArgsConstructor
public class KafkaOrderStore implements OrderStore {

  private static final EventSerde ORDER_CREATED_EVENT_SERDE = new EventSerde();

  private static final OrderSerde ORDER_SERDE = new OrderSerde();

  public static final String ORDER_STORE = "orderStore";

  private final StreamsBuilder streamsBuilder;
  private final StreamsBuilderFactoryBean factoryBean;
  @PostConstruct
  void buildPipeline() {
    KStream<String, Event> messageStream = streamsBuilder.stream("order",
        Consumed.with(Serdes.String(), ORDER_CREATED_EVENT_SERDE));
    messageStream.map((s, orderEvent) -> {
              if (orderEvent instanceof OrderCreated) {
                return new KeyValue<>(s,
                    new Order(s, ((OrderCreated) orderEvent).getOrderedItems(), Status.OPEN));
              } else {
                OrderUpdated orderUpdated = (OrderUpdated) orderEvent;
                return new KeyValue<>(s,
                    new Order(s, orderUpdated.getOrderedItems(), orderUpdated.getStatus()));
              }
            }
        )
        .groupBy((s, order) -> order.getId(), Grouped.with(Serdes.String(), ORDER_SERDE))
        .reduce((order, orderNext) -> {
          if (orderNext.getOrderedItems() == null) {
            orderNext.setOrderedItems(new ArrayList<>());
          }
          orderNext.getOrderedItems().addAll(order.getOrderedItems());
          return orderNext;
        }, Materialized.as(ORDER_STORE));
  }

  @Override
  public Order getOrderById(String id) {
    KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
    ReadOnlyKeyValueStore<String, Order> orderStore = kafkaStreams.store(
        StoreQueryParameters.fromNameAndType(ORDER_STORE, QueryableStoreTypes.keyValueStore()));
    return orderStore.get(id);
  }
}
