CREATE TABLE country (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    code       VARCHAR(2)   NOT NULL,
    currency   VARCHAR(3),
    enabled    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uq_country_code UNIQUE (code)
);

CREATE TABLE city (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    state      VARCHAR(100),
    country_id INTEGER NOT NULL REFERENCES country (id),
    enabled    BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
CREATE INDEX idx_city_country ON city (country_id);
CREATE INDEX idx_city_name ON city (name);

-- Seed countries for the existing seed hotels (V4__HotelSupplierMapCityAndSeedHotels.sql)
INSERT INTO country (name, code, currency, enabled, created_at, updated_at) VALUES
    ('India', 'IN', 'INR', TRUE, NOW(), NOW()),
    ('United Arab Emirates', 'AE', 'AED', TRUE, NOW(), NOW());

-- Seed cities with the same ids already referenced by market_place_hotel.city_id
-- (1=Bengaluru, 2=Goa, 3=Udaipur, 4=Dubai) so existing hotel rows keep working unchanged.
INSERT INTO city (id, name, state, country_id, enabled, created_at, updated_at)
SELECT 1, 'Bengaluru', 'Karnataka', c.id, TRUE, NOW(), NOW() FROM country c WHERE c.code = 'IN'
UNION ALL
SELECT 2, 'Goa', 'Goa', c.id, TRUE, NOW(), NOW() FROM country c WHERE c.code = 'IN'
UNION ALL
SELECT 3, 'Udaipur', 'Rajasthan', c.id, TRUE, NOW(), NOW() FROM country c WHERE c.code = 'IN'
UNION ALL
SELECT 4, 'Dubai', NULL, c.id, TRUE, NOW(), NOW() FROM country c WHERE c.code = 'AE';

SELECT setval(pg_get_serial_sequence('city', 'id'), (SELECT MAX(id) FROM city));

-- Enforce the Hotel -> City relationship (country is derived transitively via city -> country).
ALTER TABLE market_place_hotel
    ADD CONSTRAINT fk_mph_city FOREIGN KEY (city_id) REFERENCES city (id);

ALTER TABLE hotel_supplier_map
    ADD CONSTRAINT fk_hsm_city FOREIGN KEY (city_id) REFERENCES city (id);
