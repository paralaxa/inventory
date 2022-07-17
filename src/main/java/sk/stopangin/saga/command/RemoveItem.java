package sk.stopangin.saga.command;

import lombok.Data;
import sk.stopangin.saga.common.OrderedItem;

@Data
public class RemoveItem implements Command {

  private String userId;
  private OrderedItem orderedItem;
}
