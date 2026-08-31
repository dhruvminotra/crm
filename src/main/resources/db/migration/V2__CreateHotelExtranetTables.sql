CREATE TABLE market_place_hotel (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(200) NOT NULL,
    city_id      INTEGER,
    city_name    VARCHAR(100),
    country_code VARCHAR(2),
    star_rating  INTEGER,
    is_cruise    BOOLEAN NOT NULL DEFAULT FALSE,
    enabled      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP
);

CREATE TABLE hotel_room (
    id            BIGSERIAL PRIMARY KEY,
    hotel_id      BIGINT NOT NULL REFERENCES market_place_hotel (id),
    room_name     VARCHAR(150) NOT NULL,
    description   VARCHAR(1000),
    max_occupancy INTEGER,
    max_adults    INTEGER,
    total_rooms   INTEGER,
    enabled       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP
);
CREATE INDEX idx_hotel_room_hotel ON hotel_room (hotel_id);

CREATE TABLE hotel_supplier_map (
    id          BIGSERIAL PRIMARY KEY,
    hotel_id    BIGINT      NOT NULL REFERENCES market_place_hotel (id),
    supplier_id BIGINT      NOT NULL REFERENCES users (user_id),
    map_type    VARCHAR(20) NOT NULL DEFAULT 'SUPPLIER',
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT uq_hotel_supplier_map UNIQUE (hotel_id, supplier_id, map_type)
);
CREATE INDEX idx_hsm_supplier ON hotel_supplier_map (supplier_id);

CREATE TABLE rate_plan (
    id                        BIGSERIAL PRIMARY KEY,
    hotel_id                  BIGINT       NOT NULL REFERENCES market_place_hotel (id),
    room_id                   BIGINT       NOT NULL REFERENCES hotel_room (id),
    supplier_id               BIGINT       NOT NULL REFERENCES users (user_id),
    plan_name                 VARCHAR(200) NOT NULL,
    meal_plan                 VARCHAR(10)  NOT NULL,
    currency                  VARCHAR(3)   NOT NULL,
    max_occupancy             INTEGER      NOT NULL,
    max_adults                INTEGER      NOT NULL,
    max_child_with_max_adults INTEGER,
    max_child_without_bed     INTEGER,
    min_cwb_age               INTEGER,
    min_cwob_age              INTEGER,
    min_adult_age             INTEGER,
    min_length_of_stay        INTEGER,
    promo_code                VARCHAR(50),
    inclusions                VARCHAR(1000),
    contract_type             VARCHAR(20),
    commission_percent        NUMERIC(7, 2),
    cancellation_policy       TEXT,
    enabled                   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at                TIMESTAMP,
    updated_at                TIMESTAMP
);
CREATE INDEX idx_rate_plan_hotel ON rate_plan (hotel_id);
CREATE INDEX idx_rate_plan_room ON rate_plan (room_id);

CREATE TABLE supplier_package_pricing (
    id                 BIGSERIAL PRIMARY KEY,
    rate_plan_id       BIGINT     NOT NULL REFERENCES rate_plan (id),
    hotel_id           BIGINT     NOT NULL REFERENCES market_place_hotel (id),
    supplier_id        BIGINT     NOT NULL REFERENCES users (user_id),
    travel_start_date  DATE       NOT NULL,
    travel_end_date    DATE       NOT NULL,
    applicable_days    VARCHAR(30),
    currency           VARCHAR(3) NOT NULL,
    single_sharing     NUMERIC(12, 2),
    twin_sharing       NUMERIC(12, 2),
    extra_adult        NUMERIC(12, 2),
    child_with_bed     NUMERIC(12, 2),
    child_without_bed  NUMERIC(12, 2),
    infant             NUMERIC(12, 2),
    min_stay           INTEGER,
    cut_off_days       INTEGER,
    commission_percent NUMERIC(7, 2),
    promo_code         VARCHAR(50),
    audited            BOOLEAN NOT NULL DEFAULT TRUE,
    enabled            BOOLEAN NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP
);
CREATE INDEX idx_spp_hotel_window ON supplier_package_pricing (hotel_id, travel_start_date, travel_end_date);
CREATE INDEX idx_spp_rate_plan ON supplier_package_pricing (rate_plan_id);

CREATE TABLE package_inventory (
    id            BIGSERIAL PRIMARY KEY,
    hotel_id      BIGINT NOT NULL REFERENCES market_place_hotel (id),
    room_id       BIGINT NOT NULL REFERENCES hotel_room (id),
    supplier_id   BIGINT NOT NULL REFERENCES users (user_id),
    stay_date     DATE   NOT NULL,
    allocated     INTEGER NOT NULL DEFAULT 0,
    sold          INTEGER NOT NULL DEFAULT 0,
    cut_off_days  INTEGER,
    close_for_sale BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    CONSTRAINT uq_package_inventory UNIQUE (hotel_id, room_id, supplier_id, stay_date)
);
CREATE INDEX idx_pkg_inv_hotel_date ON package_inventory (hotel_id, stay_date);

CREATE TABLE hotel_policy (
    id                 BIGSERIAL PRIMARY KEY,
    hotel_id           BIGINT      NOT NULL REFERENCES market_place_hotel (id),
    supplier_id        BIGINT      NOT NULL REFERENCES users (user_id),
    policy_type        VARCHAR(20) NOT NULL,
    travel_start_date  DATE,
    travel_end_date    DATE,
    policy_json        TEXT,
    commission_percent NUMERIC(7, 2),
    enabled            BOOLEAN NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP
);
CREATE INDEX idx_hotel_policy_hotel ON hotel_policy (hotel_id, policy_type);

CREATE TABLE product_promotion_offer (
    id                BIGSERIAL PRIMARY KEY,
    hotel_id          BIGINT       NOT NULL REFERENCES market_place_hotel (id),
    supplier_id       BIGINT       NOT NULL REFERENCES users (user_id),
    type              VARCHAR(30)  NOT NULL,
    title             VARCHAR(200) NOT NULL,
    category          VARCHAR(50),
    offer_start_date  DATE NOT NULL,
    offer_end_date    DATE NOT NULL,
    travel_start_date DATE NOT NULL,
    travel_end_date   DATE NOT NULL,
    discount_type     VARCHAR(20)   NOT NULL,
    discount_value    NUMERIC(12, 2) NOT NULL,
    room_ids          VARCHAR(500),
    meal_plans        VARCHAR(100),
    min_duration      INTEGER,
    days_in_advance   INTEGER,
    status            VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP
);
CREATE INDEX idx_promo_hotel_type ON product_promotion_offer (hotel_id, type, status);
