package ie.felipelabs.orders.web;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;
import static ie.felipelabs.orders.web.DomainExceptions.*;
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFound.class)
    ProblemDetail notFound(NotFound e) { return problem(HttpStatus.NOT_FOUND, "Resource not found", e.getMessage()); }
    @ExceptionHandler(IdempotencyConflict.class)
    ProblemDetail conflict(IdempotencyConflict e) { return problem(HttpStatus.CONFLICT, "Idempotency conflict", e.getMessage()); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail duplicate(DataIntegrityViolationException e) { return problem(HttpStatus.CONFLICT, "Duplicate order", "The client reference is already in use"); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail invalidRequest(MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getField() + ": " + x.getDefaultMessage()).collect(Collectors.joining("; "));
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", detail);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail invalidParameter(IllegalArgumentException e) { return problem(HttpStatus.BAD_REQUEST, "Invalid request", e.getMessage()); }
    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(status, detail);
        body.setTitle(title);
        return body;
    }
}
