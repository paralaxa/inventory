package sk.stopangin.saga.event;

public interface EventPublisher {

  void publish(Event event);

}