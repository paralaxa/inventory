package sk.stopangin.saga.service;

import java.util.List;
import sk.stopangin.saga.common.CreateOrderRequest;
import sk.stopangin.saga.common.OrderedItem;
import sk.stopangin.saga.domain.Order;

public interface OrderService {

  void createOrder(CreateOrderRequest createOrderRequest);

  void addItems(String id, List<OrderedItem> orderedItems);

  void cancelOrder(String orderId);

  Order getOrderById(String id);
}
