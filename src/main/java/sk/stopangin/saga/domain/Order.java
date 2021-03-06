package sk.stopangin.saga.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stopangin.saga.common.OrderedItem;
import sk.stopangin.saga.common.Status;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

  private String id;
  private List<OrderedItem> orderedItems = new ArrayList<>();
  private Status status;

  public Order withOrderedItem(OrderedItem orderedItem) {
    orderedItems.add(orderedItem);
    return this;
  }


}
