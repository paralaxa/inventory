package sk.stopangin.saga;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaCompletionMode;
import org.apache.camel.model.SagaPropagation;
import org.springframework.stereotype.Component;
import sk.stopangin.saga.command.OrderCommandHandler;
import sk.stopangin.saga.command.RemoveItem;

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

    from("direct:orderUpdate")
        .choice()
        .when(exchange -> exchange.getMessage().getBody() instanceof RemoveItem)
        .bean(orderCommandHandler, "remove")
        .otherwise()
        .bean(orderCommandHandler, "add")
        .end()
        .to("kafka:order");

    from("direct:orderCancel")
        .log("Direct cancel!")
        .to("saga:compensate");
  }


}
