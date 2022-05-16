package sk.stopangin.saga.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.springframework.stereotype.Service;

import sk.stopangin.saga.domain.Order;
import sk.stopangin.saga.event.EventPublisher;
import sk.stopangin.saga.event.OrderCanceled;
import sk.stopangin.saga.event.OrderCreated;
import sk.stopangin.saga.event.OrderUpdated;
import sk.stopangin.saga.event.PaymentUpdated;
import sk.stopangin.saga.store.OrderStore;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderCommandHandlerImpl implements
    OrderCommandHandler {

  private final EventPublisher eventPublisher;
  private final OrderStore orderStore;

  @Override
  public OrderCreated create(CreateOrder createOrder) {
    return new OrderCreated(createOrder.getId(), createOrder.getOrderedItems());
  }

  @Override
  public void update(UpdateOrder updateOrder) {
    eventPublisher.publish(
        new OrderUpdated(updateOrder.getId(), updateOrder.getStatus(),
            updateOrder.getOrderedItems()));
  }

  @Override
  public void cancel(String id) {
    Order order = orderStore.getOrderById(id);
    eventPublisher.publish(new OrderCanceled(id, order.getOrderedItems()));
  }

  @Override
  public void updatePayment(PaymentUpdated paymentUpdated) {
    log.info("Updating order for: {}", paymentUpdated);
  }
}
