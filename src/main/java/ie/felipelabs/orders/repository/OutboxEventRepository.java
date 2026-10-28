package ie.felipelabs.orders.repository;
import ie.felipelabs.orders.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.*;
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findTop50ByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(OutboxStatus status, Instant now);
}
