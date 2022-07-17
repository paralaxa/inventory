package sk.stopangin.saga.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sk.stopangin.saga.domain.Order;
import sk.stopangin.saga.event.EventPublisher;
import sk.stopangin.saga.event.ItemAdded;
import sk.stopangin.saga.event.ItemRemoved;
import sk.stopangin.saga.event.OrderCanceled;
import sk.stopangin.saga.event.OrderCreated;
import sk.stopangin.saga.event.OrderUpdated;
import sk.stopangin.saga.event.PaymentUpdated;
import sk.stopangin.saga.store.OrderStore;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultOrderCommandHandler implements
    OrderCommandHandler {

  private final EventPublisher eventPublisher;
  private final OrderStore orderStore;

  @Override
  public OrderCreated create(CreateOrder createOrder) {
    return new OrderCreated(createOrder.getId(), createOrder.getUserId(),
        createOrder.getOrderedItems());
  }

  @Override
  public void update(UpdateOrder updateOrder) {
    eventPublisher.publish(
        new OrderUpdated(updateOrder.getUserId(),
            updateOrder.getOrderedItems()));
  }

  @Override
  public void cancel(String id) {
    Order order = orderStore.getOrderById(id);
    eventPublisher.publish(new OrderCanceled(id, order.getOrderedItems()));
  }

  @Override
  public ItemAdded add(AddItem item) {
    ItemAdded itemAdded = new ItemAdded();
    itemAdded.setOrderedItem(item.getOrderedItem());
    itemAdded.setId(item.getUserId());
    return itemAdded;
  }

  @Override
  public ItemRemoved remove(RemoveItem item) {
    ItemRemoved itemRemoved = new ItemRemoved();
    itemRemoved.setOrderedItem(item.getOrderedItem());
    itemRemoved.setId(item.getUserId());
    return itemRemoved;
  }

  @Override
  public void updatePayment(PaymentUpdated paymentUpdated) {
    log.info("Updating order for: {}", paymentUpdated);
  }
}
