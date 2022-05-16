package sk.stopangin.saga.command;

import sk.stopangin.saga.event.PaymentUpdated;

public interface OrderCommandHandler {

  void create(String lraId, CreateOrder createOrder);

  void update(UpdateOrder updateOrder);

  void cancel(String id);

  void updatePayment(PaymentUpdated paymentUpdated);
}
