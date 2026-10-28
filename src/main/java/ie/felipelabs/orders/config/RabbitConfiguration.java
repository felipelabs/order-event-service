package ie.felipelabs.orders.config;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
@Configuration
public class RabbitConfiguration {
    @Bean TopicExchange orderExchange(@Value("${app.messaging.exchange}") String name) {
        return ExchangeBuilder.topicExchange(name).durable(true).build();
    }
    @Bean Queue orderCreatedQueue(@Value("${app.messaging.order-created-queue}") String name) {
        return QueueBuilder.durable(name).build();
    }
    @Bean Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange orderExchange,
            @Value("${app.messaging.order-created-routing-key}") String routingKey) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with(routingKey);
    }
}
