package sk.stopangin.saga.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PaymentUpdated.class, name = "PaymentUpdated"),
    @JsonSubTypes.Type(value = OrderCreated.class, name = "OrderCreated"),
    @JsonSubTypes.Type(value = OrderUpdated.class, name = "OrderUpdated"),
    @JsonSubTypes.Type(value = OrderCanceled.class, name = "OrderCanceled")})
public interface Event {

  String getId();
}
