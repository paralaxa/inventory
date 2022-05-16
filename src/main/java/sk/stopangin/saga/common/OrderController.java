package sk.stopangin.saga.common;

import sk.stopangin.saga.domain.Order;

public interface OrderController {

  Order getOrderById(String id);

  void create(CreateOrderRequest createOrderRequest);

  void addItems(String id, OrderedItem orderedItem);
}
