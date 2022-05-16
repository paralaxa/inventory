package sk.stopangin.saga.command;

import java.util.List;
import lombok.Data;
import sk.stopangin.saga.common.OrderedItem;

@Data
public class CreateOrder implements Command {

  private String id;
  private List<OrderedItem> orderedItems;
}
