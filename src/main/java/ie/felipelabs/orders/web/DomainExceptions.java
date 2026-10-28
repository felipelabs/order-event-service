package ie.felipelabs.orders.web;
public final class DomainExceptions {
    private DomainExceptions() {}
    public static class NotFound extends RuntimeException { public NotFound(String message) { super(message); } }
    public static class IdempotencyConflict extends RuntimeException { public IdempotencyConflict(String message) { super(message); } }
}
