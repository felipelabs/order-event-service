package ie.felipelabs.orders.web.dto;
import ie.felipelabs.orders.domain.CustomerOrder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record OrderResponse(UUID id, String clientReference, String customerEmail, BigDecimal amount,
        String currency, String status, Instant createdAt) {
    public static OrderResponse from(CustomerOrder o) {
        return new OrderResponse(o.getId(), o.getClientReference(), o.getCustomerEmail(), o.getAmount(),
                o.getCurrency(), o.getStatus().name(), o.getCreatedAt());
    }
}
