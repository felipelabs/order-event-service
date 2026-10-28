package ie.felipelabs.orders.messaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
@Component
public class OrderCreatedAuditListener {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedAuditListener.class);
    @RabbitListener(queues = "${app.messaging.order-created-queue}")
    public void receive(Message message) {
        log.info("Received OrderCreated event messageId={}", message.getMessageProperties().getMessageId());
    }
}
