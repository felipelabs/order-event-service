package ie.felipelabs.orders.domain;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record OrderCreatedEvent(UUID eventId, UUID orderId, String clientReference, String customerEmail,
        BigDecimal amount, String currency, Instant occurredAt) {}
