INSERT INTO users (role, roletype, activated, name, email, e_verified, e_activated,
                   mobile, m_verified, m_activated, password, base_currency, uuid)
VALUES ('A', 1, 'Y', 'CRM Admin', 'admin@veyora.com', 'Y', 'Y',
        '9800000001', 'Y', 'Y',
        '$2a$10$FL7WCxcy8B1/pPRezn/sdO8dADLU1C442gcMsv.s.yc.xC7EzrlHe', 'INR',
        '3f1c2a10-0000-4000-8000-000000000001'),
       ('E', 8, 'Y', 'Demo Hotelier', 'hotelier@veyora.com', 'Y', 'Y',
        '9800000002', 'Y', 'Y',
        '$2a$10$FL7WCxcy8B1/pPRezn/sdO8dADLU1C442gcMsv.s.yc.xC7EzrlHe', 'INR',
        '3f1c2a10-0000-4000-8000-000000000002');

INSERT INTO users (role, roletype, activated, name, email, mobile, password,
                   base_currency, pageownerid, uuid)
VALUES ('D', 12, 'Y', 'Front Desk One', 'desk@veyora.com', '9800000003',
        '$2a$10$FL7WCxcy8B1/pPRezn/sdO8dADLU1C442gcMsv.s.yc.xC7EzrlHe', 'INR',
        2, '3f1c2a10-0000-4000-8000-000000000003');

INSERT INTO user_hist (user_id, history)
VALUES (1, 'User created by system seed'),
       (2, 'User created by system seed'),
       (3, 'User created by desk admin 2');

INSERT INTO market_place_hotel (name, city_id, city_name, country_code, star_rating, is_cruise, enabled, created_at, updated_at)
VALUES ('The Grand Demo Palace', 1, 'Bengaluru', 'IN', 5, FALSE, TRUE, NOW(), NOW());

INSERT INTO hotel_room (hotel_id, room_name, description, max_occupancy, max_adults, total_rooms, enabled, created_at, updated_at)
VALUES (1, 'Deluxe Room', 'Deluxe room with city view', 3, 2, 20, TRUE, NOW(), NOW()),
       (1, 'Executive Suite', 'Suite with living area', 4, 3, 8, TRUE, NOW(), NOW());

INSERT INTO hotel_supplier_map (hotel_id, supplier_id, map_type, created_at, updated_at)
VALUES (1, 2, 'SUPPLIER', NOW(), NOW());
