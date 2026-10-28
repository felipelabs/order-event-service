package ie.felipelabs.orders.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name = "outbox_events")
public class OutboxEvent {
    public static final int MAX_ATTEMPTS = 8;
    @Id private UUID id;
    @Column(name = "aggregate_id", nullable = false) private UUID aggregateId;
    @Column(name = "event_type", nullable = false, length = 120) private String eventType;
    @Column(nullable = false, columnDefinition = "text") private String payload;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private OutboxStatus status;
    @Column(nullable = false) private int attempts;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "next_attempt_at", nullable = false) private Instant nextAttemptAt;
    @Column(name = "published_at") private Instant publishedAt;
    @Column(name = "last_error", length = 1000) private String lastError;
    protected OutboxEvent() {}
    public OutboxEvent(UUID id, UUID aggregateId, String eventType, String payload, Instant now) {
        this.id = id; this.aggregateId = aggregateId; this.eventType = eventType;
        this.payload = payload; status = OutboxStatus.PENDING; createdAt = now; nextAttemptAt = now;
    }
    public void markPublished(Instant now) { status = OutboxStatus.PUBLISHED; publishedAt = now; lastError = null; }
    public void recordFailure(String message, Instant now) {
        attempts++;
        lastError = message == null ? "Unknown broker error" : message.substring(0, Math.min(1000, message.length()));
        if (attempts >= MAX_ATTEMPTS) { status = OutboxStatus.DEAD; return; }
        nextAttemptAt = now.plusSeconds(Math.min(300L, 1L << Math.min(attempts, 8)));
    }
    public UUID getId() { return id; }
    public String getPayload() { return payload; }
    public OutboxStatus getStatus() { return status; }
}
