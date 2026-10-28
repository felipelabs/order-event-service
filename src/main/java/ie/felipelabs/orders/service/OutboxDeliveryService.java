package ie.felipelabs.orders.service;
import ie.felipelabs.orders.domain.*;
import ie.felipelabs.orders.repository.OutboxEventRepository;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
@Service
public class OutboxDeliveryService {
    private final OutboxEventRepository events;
    private final RabbitTemplate rabbit;
    private final Clock clock;
    private final String exchange, routingKey;
    public OutboxDeliveryService(OutboxEventRepository events, RabbitTemplate rabbit, Clock clock,
            @Value("${app.messaging.exchange}") String exchange,
            @Value("${app.messaging.order-created-routing-key}") String routingKey) {
        this.events = events; this.rabbit = rabbit; this.clock = clock; this.exchange = exchange; this.routingKey = routingKey;
    }
    @Transactional
    public void deliver(UUID id) {
        OutboxEvent event = events.findById(id).orElse(null);
        if (event == null || event.getStatus() != OutboxStatus.PENDING) return;
        try {
            CorrelationData correlation = new CorrelationData(event.getId().toString());
            rabbit.convertAndSend(exchange, routingKey, event.getPayload(), message -> {
                message.getMessageProperties().setMessageId(event.getId().toString());
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                message.getMessageProperties().setContentType("application/json");
                return message;
            }, correlation);
            CorrelationData.Confirm confirm = correlation.getFuture().get(10, TimeUnit.SECONDS);
            if (!confirm.isAck()) throw new IllegalStateException("RabbitMQ did not confirm the event");
            if (correlation.getReturned() != null) throw new IllegalStateException("RabbitMQ could not route the event");
            event.markPublished(clock.instant());
        } catch (Exception failure) {
            if (failure instanceof InterruptedException) Thread.currentThread().interrupt();
            event.recordFailure(failure.getMessage(), clock.instant());
        }
    }
}
