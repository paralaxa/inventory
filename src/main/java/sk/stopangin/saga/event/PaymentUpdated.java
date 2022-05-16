package sk.stopangin.saga.event;

import lombok.Data;
import sk.stopangin.saga.common.Status;

@Data
public class PaymentUpdated implements Event {

  private String id;
  private Status status;

}
