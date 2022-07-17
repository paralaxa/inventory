package sk.stopangin.saga.service;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;
import sk.stopangin.saga.command.AddItem;
import sk.stopangin.saga.command.CreateOrder;
import sk.stopangin.saga.command.RemoveItem;
import sk.stopangin.saga.command.UpdateOrder;
import sk.stopangin.saga.common.CreateOrderRequest;
import sk.stopangin.saga.common.OrderedItem;
import sk.stopangin.saga.common.SecurityContext;
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
    createOrder.setUserId(SecurityContext.getContext());
    createOrder.setOrderedItems(createOrderRequest.getOrderedItems());
    producerTemplate.sendBody("direct:orderCreate", createOrder);
  }

  @Override
  public void cancelOrder(String userId) {
    UpdateOrder updateOrder = new UpdateOrder();
    updateOrder.setUserId(userId);
    updateOrder.setStatus(Status.CANCELED);
    producerTemplate.sendBody("direct:orderCancel", updateOrder);
  }

  @Override
  public void addItems(String id, List<OrderedItem> orderedItems) {
    //todo update cez request a separatny command
    UpdateOrder updateOrder = new UpdateOrder();
    updateOrder.setUserId(id);
    updateOrder.setOrderedItems(orderedItems);
    producerTemplate.sendBody("direct:orderUpdate", updateOrder);
  }

  @Override
  public void addItem(OrderedItem orderedItem) {
    //todo OrderedItemRequest
    if (orderStore.getOrderById(SecurityContext.getContext()) == null) {
      CreateOrderRequest createOrderRequest = new CreateOrderRequest();
      createOrderRequest.setOrderedItems(Collections.singletonList(orderedItem));
      createOrder(createOrderRequest);
    } else {
      AddItem addItem = new AddItem();
      addItem.setOrderedItem(orderedItem);
      addItem.setUserId(SecurityContext.getContext());
      producerTemplate.sendBody("direct:orderUpdate", addItem);
    }
  }


  @Override
  public void removeItem(OrderedItem orderedItem) {
    //todo OrderedItemRequest
    RemoveItem removeItem = new RemoveItem();
    removeItem.setOrderedItem(orderedItem);
    removeItem.setUserId(SecurityContext.getContext());
    producerTemplate.sendBody("direct:orderUpdate", removeItem);
  }

  @Override
  public Order getOrderById(String id) {
    return orderStore.getOrderById(id);
  }
}
