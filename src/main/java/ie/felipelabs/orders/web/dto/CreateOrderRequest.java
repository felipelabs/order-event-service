package ie.felipelabs.orders.web.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record CreateOrderRequest(@NotBlank @Size(max = 80) String clientReference,
        @NotBlank @Email @Size(max = 254) String customerEmail,
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 10, fraction = 2) BigDecimal amount,
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String currency) {}
