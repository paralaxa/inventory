package sk.stopangin.saga;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaCompletionMode;
import org.apache.camel.model.SagaPropagation;
import org.springframework.stereotype.Component;
import sk.stopangin.saga.command.OrderCommandHandler;
import sk.stopangin.saga.common.Status;
import sk.stopangin.saga.event.PaymentUpdated;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaRouterBuilder extends RouteBuilder {

  private final OrderCommandHandler orderCommandHandler;

  @Override
  public void configure() throws Exception {
    restConfiguration().port(8182).host("localhost");

    from("direct:orderCreate")
        .saga()
        .timeout(Duration.ofSeconds(30))
        .propagation(SagaPropagation.REQUIRES_NEW)
        .completionMode(SagaCompletionMode.MANUAL)
        .compensation("direct:orderCancel")
        .bean(orderCommandHandler, "create")
        .to("kafka:order");

    from(
        "kafka:paymentUpdates") //todo vsetky update k paymentu (success aj failed budu chodit cez 1 topic)
        .saga()
        .propagation(SagaPropagation.MANDATORY)
        .choice()
        .when(exchange -> Status.CANCELED == ((PaymentUpdated) exchange.getMessage()
            .getBody()).getStatus())
        .log("Saga compensated")
        .to("saga:compensate")
        .otherwise()
        .bean(orderCommandHandler, "updatePayment") //todo transform na UpdateCommand
        .log("Saga completed")
        .to("saga:complete")
        .end();

    from("direct:orderCancel")
        .log("Direct cancel!")
        .to("saga:compensate");
  }


}
