package sk.stopangin.saga.event;

import lombok.Data;
import sk.stopangin.saga.common.OrderedItem;

@Data
public class ItemRemoved implements Event {

  private String id;
  private OrderedItem orderedItem;
}
