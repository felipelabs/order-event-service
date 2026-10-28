package ie.felipelabs.orders.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name = "customer_orders")
public class CustomerOrder {
    @Id private UUID id;
    @Column(name = "client_reference", nullable = false, unique = true, length = 80) private String clientReference;
    @Column(name = "customer_email", nullable = false, length = 254) private String customerEmail;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal amount;
    @Column(nullable = false, length = 3) private String currency;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private OrderStatus status;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    protected CustomerOrder() {}
    public CustomerOrder(String reference, String email, BigDecimal amount, String currency, Instant now) {
        id = UUID.randomUUID(); clientReference = reference; customerEmail = email;
        this.amount = amount; this.currency = currency; status = OrderStatus.ACCEPTED; createdAt = now;
    }
    public UUID getId() { return id; }
    public String getClientReference() { return clientReference; }
    public String getCustomerEmail() { return customerEmail; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
