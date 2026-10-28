package ie.felipelabs.orders.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ie.felipelabs.orders.domain.*;
import ie.felipelabs.orders.repository.*;
import ie.felipelabs.orders.web.DomainExceptions.*;
import ie.felipelabs.orders.web.dto.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.Locale;
import java.util.UUID;
@Service
public class OrderService {
    private final CustomerOrderRepository orders;
    private final OutboxEventRepository outbox;
    private final ObjectMapper mapper;
    private final Clock clock;
    public OrderService(CustomerOrderRepository orders, OutboxEventRepository outbox, ObjectMapper mapper, Clock clock) {
        this.orders = orders; this.outbox = outbox; this.mapper = mapper; this.clock = clock;
    }
    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        String reference = request.clientReference().trim();
        String email = request.customerEmail().trim().toLowerCase(Locale.ROOT);
        String currency = request.currency().toUpperCase(Locale.ROOT);
        var existing = orders.findByClientReference(reference);
        if (existing.isPresent()) {
            CustomerOrder order = existing.get();
            if (!order.getCustomerEmail().equals(email) || order.getAmount().compareTo(request.amount()) != 0 || !order.getCurrency().equals(currency))
                throw new IdempotencyConflict("clientReference was reused with different order details");
            return OrderResponse.from(order);
        }
        Instant now = clock.instant();
        CustomerOrder order = orders.save(new CustomerOrder(reference, email, request.amount(), currency, now));
        UUID eventId = UUID.randomUUID();
        OrderCreatedEvent event = new OrderCreatedEvent(eventId, order.getId(), reference, email,
                order.getAmount(), currency, now);
        try { outbox.save(new OutboxEvent(eventId, order.getId(), "OrderCreated", mapper.writeValueAsString(event), now)); }
        catch (JsonProcessingException e) { throw new IllegalStateException("Could not serialize order event", e); }
        return OrderResponse.from(order);
    }
    @Transactional(readOnly = true)
    public Page<OrderResponse> list(Pageable pageable) { return orders.findAll(pageable).map(OrderResponse::from); }
    @Transactional(readOnly = true)
    public OrderResponse get(UUID id) {
        return OrderResponse.from(orders.findById(id).orElseThrow(() -> new NotFound("Order not found")));
    }
}
