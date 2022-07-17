package sk.stopangin.saga.command;

import sk.stopangin.saga.event.ItemAdded;
import sk.stopangin.saga.event.ItemRemoved;
import sk.stopangin.saga.event.OrderCreated;
import sk.stopangin.saga.event.PaymentUpdated;

public interface OrderCommandHandler {

  OrderCreated create(CreateOrder createOrder);

  ItemAdded add(AddItem item);

  ItemRemoved remove(RemoveItem item);

  void update(UpdateOrder updateOrder);

  void cancel(String id);

  void updatePayment(PaymentUpdated paymentUpdated);
}
