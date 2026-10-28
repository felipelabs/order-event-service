CREATE TABLE customer_orders (
    id UUID PRIMARY KEY,
    client_reference VARCHAR(80) NOT NULL UNIQUE,
    customer_email VARCHAR(254) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(24) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL REFERENCES customer_orders(id),
    event_type VARCHAR(120) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(24) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    next_attempt_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMPTZ,
    last_error VARCHAR(1000)
);
CREATE INDEX outbox_ready_idx ON outbox_events(status, next_attempt_at, created_at);
