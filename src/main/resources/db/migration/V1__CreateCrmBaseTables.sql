-- CRM base tables: users (extranet + internal roles)
CREATE TABLE app_user (
    id                BIGSERIAL PRIMARY KEY,
    email             VARCHAR(100) NOT NULL UNIQUE,
    password          VARCHAR(100) NOT NULL,
    display_name      VARCHAR(100),
    role              VARCHAR(30)  NOT NULL,
    business_currency VARCHAR(3),
    enabled           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP
);
