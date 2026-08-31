ALTER TABLE hotel_supplier_map ADD COLUMN city_id INT4;
ALTER TABLE hotel_supplier_map ADD COLUMN hotel_name VARCHAR(200);

UPDATE hotel_supplier_map m
SET city_id = h.city_id, hotel_name = h.name
FROM market_place_hotel h
WHERE h.id = m.hotel_id;

INSERT INTO market_place_hotel (name, city_id, city_name, country_code, star_rating, is_cruise, enabled, created_at, updated_at)
VALUES ('Veyora Grand Bengaluru', 1, 'Bengaluru', 'IN', 5, FALSE, TRUE, NOW(), NOW()),
       ('Sea Pearl Resort Goa',   2, 'Goa',       'IN', 4, FALSE, TRUE, NOW(), NOW()),
       ('Lake View Palace Udaipur', 3, 'Udaipur', 'IN', 5, FALSE, TRUE, NOW(), NOW()),
       ('City Central Inn Dubai', 4, 'Dubai',     'AE', 3, FALSE, TRUE, NOW(), NOW());

INSERT INTO hotel_room (hotel_id, room_name, description, max_occupancy, max_adults, total_rooms, enabled, created_at, updated_at)
VALUES (2, 'Deluxe Room', 'Deluxe room with garden view', 3, 2, 30, TRUE, NOW(), NOW()),
       (2, 'Club Room', 'Club floor room with lounge access', 3, 2, 12, TRUE, NOW(), NOW()),
       (3, 'Beach Cottage', 'Cottage facing the beach', 4, 3, 15, TRUE, NOW(), NOW()),
       (3, 'Superior Room', 'Superior room with balcony', 3, 2, 25, TRUE, NOW(), NOW()),
       (4, 'Lake Facing Room', 'Room overlooking the lake', 3, 2, 20, TRUE, NOW(), NOW()),
       (4, 'Royal Suite', 'Suite with private terrace', 4, 3, 6, TRUE, NOW(), NOW()),
       (5, 'Standard Room', 'Compact city room', 2, 2, 40, TRUE, NOW(), NOW()),
       (5, 'Family Room', 'Room for families', 4, 3, 10, TRUE, NOW(), NOW());

INSERT INTO hotel_supplier_map (hotel_id, supplier_id, map_type, city_id, hotel_name, created_at, updated_at)
SELECT h.id, 2, 'SUPPLIER', h.city_id, h.name, NOW(), NOW()
FROM market_place_hotel h
WHERE h.id IN (2, 3, 4, 5);
