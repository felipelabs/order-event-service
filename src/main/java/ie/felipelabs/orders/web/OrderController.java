package ie.felipelabs.orders.web;
import ie.felipelabs.orders.service.OrderService;
import ie.felipelabs.orders.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;
@RestController @RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }
    @PostMapping public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + response.id())).body(response);
    }
    @GetMapping public Page<OrderResponse> list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1 || size > 100) throw new IllegalArgumentException("page must be >= 0 and size must be between 1 and 100");
        return service.list(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    @GetMapping("/{id}") public OrderResponse get(@PathVariable UUID id) { return service.get(id); }
}
