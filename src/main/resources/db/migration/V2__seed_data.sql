

-- USERS
INSERT INTO users (id, email, password_hash, role)
VALUES
    (1, 'user@test.com',  'test_hash', 'USER'),
    (2, 'admin@test.com', 'test_hash', 'ADMIN');

-- TRIPS
INSERT INTO trips (id, user_id, title, country, city, start_date, end_date, description, cover_photo_path)
VALUES
    (1, 1, 'Berlin weekend', 'Germany', 'Berlin',
     '2026-01-10', '2026-01-12',
     'Короткая поездка в Берлин: прогулки, музеи, парки',
     '/uploads/trips/1/cover.jpg'),

    (2, 1, 'Paris trip', 'France', 'Paris',
     '2026-02-05', '2026-02-09',
     'Париж, прогулки по центру и красивые виды',
     '/uploads/trips/2/cover.jpg');

-- TAGS
INSERT INTO tags (id, name)
VALUES
    (1, 'nature'),
    (2, 'food'),
    (3, 'museum'),
    (4, 'viewpoint');

-- MEMORIES
INSERT INTO memories (id, trip_id, title, note, visited_at, lat, lon, address_label, rating)
VALUES
    (1, 1, 'Tiergarten',
     'Очень красивый парк, лучше прийти ближе к вечеру.',
     '2026-01-10 17:30:00',
     52.514536, 13.350151,
     'Tiergarten Berlin', 5),

    (2, 1, 'Brandenburg Gate',
     'Культовое место, туристов много.',
     '2026-01-10 19:10:00',
     52.516275, 13.377704,
     'Brandenburg Gate', 4),

    (3, 2, 'Louvre Museum',
     'Музей огромный, туда нужен целый день.',
     '2026-02-06 12:00:00',
     48.860611, 2.337644,
     'Louvre Museum Paris', 5),

    (4, 2, 'Eiffel Tower',
     'Лучший вид вечером, особенно с подсветкой.',
     '2026-02-07 20:00:00',
     48.858370, 2.294481,
     'Eiffel Tower', 5);

-- MEMORY PHOTOS
INSERT INTO memory_photos (id, memory_id, file_path)
VALUES
    (1, 1, '/uploads/memories/1/1.jpg'),
    (2, 1, '/uploads/memories/1/2.jpg'),
    (3, 3, '/uploads/memories/3/1.jpg'),
    (4, 4, '/uploads/memories/4/1.jpg');

-- MEMORY_TAGS (many-to-many)
INSERT INTO memory_tags (memory_id, tag_id)
VALUES
    (1, 1), -- Tiergarten -> nature
    (2, 4), -- Brandenburg Gate -> viewpoint
    (3, 3), -- Louvre -> museum
    (4, 4); -- Eiffel -> viewpoint


-- ✅ ВАЖНО: выравниваем sequence после явных id
SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT max(id) FROM users));
SELECT setval(pg_get_serial_sequence('trips', 'id'), (SELECT max(id) FROM trips));
SELECT setval(pg_get_serial_sequence('memories', 'id'), (SELECT max(id) FROM memories));
SELECT setval(pg_get_serial_sequence('memory_photos', 'id'), (SELECT max(id) FROM memory_photos));
SELECT setval(pg_get_serial_sequence('tags', 'id'), (SELECT max(id) FROM tags));
