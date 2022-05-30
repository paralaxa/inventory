package sk.stopangin.saga.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;
import sk.stopangin.saga.command.CreateOrder;
import sk.stopangin.saga.command.UpdateOrder;
import sk.stopangin.saga.common.CreateOrderRequest;
import sk.stopangin.saga.common.OrderedItem;
import sk.stopangin.saga.common.Status;
import sk.stopangin.saga.domain.Order;
import sk.stopangin.saga.store.OrderStore;

@Slf4j
@Service
@RequiredArgsConstructor
public class CamelOrderService implements OrderService {

  private final ProducerTemplate producerTemplate;

  private final OrderStore orderStore;

  @Override
  public void createOrder(CreateOrderRequest createOrderRequest) {
    CreateOrder createOrder = new CreateOrder();
    createOrder.setId(UUID.randomUUID().toString());
    createOrder.setOrderedItems(createOrderRequest.getOrderedItems());
    producerTemplate.sendBody("direct:orderCreate", createOrder);
  }

  @Override
  public void cancelOrder(String orderId) {
    UpdateOrder updateOrder = new UpdateOrder();
    updateOrder.setId(orderId);
    updateOrder.setStatus(Status.CANCELED);
    producerTemplate.sendBody("direct:orderCancel", updateOrder);
  }

  @Override
  public void addItems(String id, List<OrderedItem> orderedItems) {
    //todo update cez request a separatny command
    UpdateOrder updateOrder = new UpdateOrder();
    updateOrder.setId(id);
    updateOrder.setOrderedItems(orderedItems);
    producerTemplate.sendBody("direct:orderUpdate", updateOrder);
  }


  @Override
  public Order getOrderById(String id) {
    return orderStore.getOrderById(id);
  }
}
