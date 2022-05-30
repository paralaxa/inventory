package sk.stopangin.saga.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import sk.stopangin.saga.domain.Order;

public class Serdes {





  public static class OrderSerde implements Serializer<Order>,
      Deserializer<Order>, Serde<Order> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void configure(final Map<String, ?> configs, final boolean isKey) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Order deserialize(final String topic, final byte[] data) {
      if (data == null) {
        return null;
      }

      try {
        return OBJECT_MAPPER.readValue(data, Order.class);
      } catch (final IOException e) {
        throw new SerializationException(e);
      }
    }

    @Override
    public byte[] serialize(final String topic, final Order data) {
      if (data == null) {
        return null;
      }

      try {
        return OBJECT_MAPPER.writeValueAsBytes(data);
      } catch (final Exception e) {
        throw new SerializationException("Error serializing JSON message", e);
      }
    }

    @Override
    public void close() {
    }

    @Override
    public Serializer<Order> serializer() {
      return this;
    }

    @Override
    public Deserializer<Order> deserializer() {
      return this;
    }
  }

}
