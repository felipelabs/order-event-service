package ie.felipelabs.orders;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication @EnableScheduling
public class OrderEventApplication {
    public static void main(String[] args) { SpringApplication.run(OrderEventApplication.class, args); }
}
