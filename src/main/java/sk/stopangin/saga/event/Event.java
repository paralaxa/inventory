package sk.stopangin.saga.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sk.stopangin.saga.store.KafkaOrderStore.OrderFinalized;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PaymentUpdated.class, name = "PaymentUpdated"),
    @JsonSubTypes.Type(value = OrderCreated.class, name = "OrderCreated"),
    @JsonSubTypes.Type(value = OrderUpdated.class, name = "OrderUpdated"),
    @JsonSubTypes.Type(value = ItemAdded.class, name = "ItemAdded"),
    @JsonSubTypes.Type(value = ItemRemoved.class, name = "ItemRemoved"),
    @JsonSubTypes.Type(value = OrderCanceled.class, name = "OrderCanceled"),
    @JsonSubTypes.Type(value = OrderFinalized.class, name = "OrderFinalized")})
public interface Event {

  String getId();
}
