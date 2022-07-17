package sk.stopangin.saga.event;

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
public class OrderUpdated implements Event {

  private String id;
  private List<OrderedItem> orderedItems = new ArrayList<>();
}
