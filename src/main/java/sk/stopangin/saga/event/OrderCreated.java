package sk.stopangin.saga.event;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.stopangin.saga.common.OrderedItem;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreated implements Event {

  private String id;
  private String userId;
  private List<OrderedItem> orderedItems;
}
