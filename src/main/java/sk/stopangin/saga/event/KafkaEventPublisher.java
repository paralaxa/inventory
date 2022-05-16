package sk.stopangin.saga.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher {

  private final KafkaTemplate<String, Event> kafkaTemplate;

  @Override
  public void publish(Event event) {
    kafkaTemplate.executeInTransaction((operations) -> {
      operations.send("order", event.getId(), event).addCallback((doNotCare) -> {
      }, (throwable) -> {
        log.error("Error while publishing event: {}", event, throwable);
      });
      return true;
    });
  }

}
