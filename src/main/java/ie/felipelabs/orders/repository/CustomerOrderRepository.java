package ie.felipelabs.orders.repository;
import ie.felipelabs.orders.domain.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, UUID> {
    Optional<CustomerOrder> findByClientReference(String clientReference);
}
