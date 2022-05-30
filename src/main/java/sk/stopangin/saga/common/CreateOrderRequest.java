package sk.stopangin.saga.common;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateOrderRequest {

  @NotEmpty
  private List<OrderedItem> orderedItems;

}
