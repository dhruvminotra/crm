-- Seed: an admin, a hotelier, one hotel with rooms and the supplier mapping.
-- Password for both users is "admin123" (BCrypt), local/dev convenience only.
INSERT INTO app_user (email, password, display_name, role, business_currency, enabled, created_at, updated_at)
VALUES ('admin@veyora.com', '$2a$10$FL7WCxcy8B1/pPRezn/sdO8dADLU1C442gcMsv.s.yc.xC7EzrlHe',
        'CRM Admin', 'ADMIN', 'INR', TRUE, NOW(), NOW()),
       ('hotelier@veyora.com', '$2a$10$FL7WCxcy8B1/pPRezn/sdO8dADLU1C442gcMsv.s.yc.xC7EzrlHe',
        'Demo Hotelier', 'HOTELIER', 'INR', TRUE, NOW(), NOW());

INSERT INTO market_place_hotel (name, city_id, city_name, country_code, star_rating, is_cruise, enabled, created_at, updated_at)
VALUES ('The Grand Demo Palace', 1, 'Bengaluru', 'IN', 5, FALSE, TRUE, NOW(), NOW());

INSERT INTO hotel_room (hotel_id, room_name, description, max_occupancy, max_adults, total_rooms, enabled, created_at, updated_at)
VALUES (1, 'Deluxe Room', 'Deluxe room with city view', 3, 2, 20, TRUE, NOW(), NOW()),
       (1, 'Executive Suite', 'Suite with living area', 4, 3, 8, TRUE, NOW(), NOW());

INSERT INTO hotel_supplier_map (hotel_id, supplier_id, map_type, created_at, updated_at)
VALUES (1, 2, 'SUPPLIER', NOW(), NOW());
