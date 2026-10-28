package ie.felipelabs.orders.service;
import ie.felipelabs.orders.domain.OutboxStatus;
import ie.felipelabs.orders.repository.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Clock;
@Component
public class OutboxPublisher {
    private final OutboxEventRepository events;
    private final OutboxDeliveryService delivery;
    private final Clock clock;
    public OutboxPublisher(OutboxEventRepository events, OutboxDeliveryService delivery, Clock clock) {
        this.events = events; this.delivery = delivery; this.clock = clock;
    }
    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:5000}")
    public void publishPending() {
        events.findTop50ByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(OutboxStatus.PENDING, clock.instant())
                .forEach(event -> delivery.deliver(event.getId()));
    }
}
