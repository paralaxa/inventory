package sk.stopangin.saga.store;

import sk.stopangin.saga.domain.Order;

public interface OrderStore {

  Order getOrderById(String id);
}
