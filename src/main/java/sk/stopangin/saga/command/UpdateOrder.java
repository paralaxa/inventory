package sk.stopangin.saga.command;

import java.util.List;
import lombok.Data;
import sk.stopangin.saga.common.OrderedItem;
import sk.stopangin.saga.common.Status;

@Data
public class UpdateOrder implements Command {

  private String userId;
  private List<OrderedItem> orderedItems;
  private Status status;
}
