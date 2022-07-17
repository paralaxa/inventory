package sk.stopangin.saga.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderedItem {

  private Item item;
  @EqualsAndHashCode.Exclude
  private int count;

}
