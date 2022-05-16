package sk.stopangin.saga.common;

import java.util.List;
import lombok.Data;

@Data
public class CreateOrderRequest {

  private List<OrderedItem> orderedItems;

}
