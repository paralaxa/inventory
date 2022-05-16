package sk.stopangin.saga;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import sk.stopangin.saga.common.EventSerde;
import sk.stopangin.saga.event.Event;

@SpringBootApplication
@EnableKafka
@EnableKafkaStreams
public class SagacamelApplication {

  public static void main(String[] args) {
    SpringApplication.run(SagacamelApplication.class, args);

  }

  @Bean
  public ProducerFactory<String, Event> producerFactory() {
    final Map<String, Object> config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EventSerde.class);
    config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
    config.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
    config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    config.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "order-service");
    final DefaultKafkaProducerFactory<String, Event> factory =
        new DefaultKafkaProducerFactory<>(config);
    factory.setTransactionIdPrefix("order-service");
    return factory;
  }

  @Bean
  public KafkaTemplate<String, Event> kafkaTemplate(
      @Autowired ProducerFactory<String, Event> factory) {
    return new KafkaTemplate<>(factory);
  }

  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  KafkaStreamsConfiguration kStreamsConfig() {
    Map<String, Object> props = new HashMap<>();
    props.put(APPLICATION_ID_CONFIG, "streams-app");
    props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG,
        org.apache.kafka.common.serialization.Serdes.String().getClass().getName());
    props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, EventSerde.class);

    return new KafkaStreamsConfiguration(props);
  }
}
