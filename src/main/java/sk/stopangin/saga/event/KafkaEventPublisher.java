package sk.stopangin.saga.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher {

  private final KafkaTemplate<String, Event> kafkaTemplate;

  @Override
  public void publish(Event event) {
    publish(null, event);
  }

  @Override
  public void publish(String lraId, Event event) {
    kafkaTemplate.executeInTransaction((operations) -> {

      Message<Event> message = MessageBuilder
          .withPayload(event)
          .setHeader(Exchange.SAGA_LONG_RUNNING_ACTION, lraId)
          .setHeader(KafkaHeaders.MESSAGE_KEY, event.getId())
          .setHeader(KafkaHeaders.TOPIC, "order")
          .build();

      operations.send(message).addCallback((doNotCare) -> {
      }, (throwable) -> {
        log.error("Error while publishing event: {}", event, throwable);
      });
      return true;
    });
  }
}
