-- =========================
-- V1__init.sql
-- Travel Memory Map schema
-- =========================

-- USERS
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(120) NOT NULL UNIQUE,
                       password_hash TEXT NOT NULL,
                       role VARCHAR(10) NOT NULL

);

-- TRIPS
CREATE TABLE trips (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,

                       title VARCHAR(140) NOT NULL,
                       country VARCHAR(80) NOT NULL,
                       city VARCHAR(80) NOT NULL,

                       start_date DATE NOT NULL,
                       end_date DATE NOT NULL,

                       description TEXT,

    -- ✅ Обложка поездки (самый простой вариант)
                       cover_photo_path TEXT

);

-- MEMORIES (точки на карте)
CREATE TABLE memories (
                          id BIGSERIAL PRIMARY KEY,
                          trip_id BIGINT NOT NULL REFERENCES trips(id) ON DELETE CASCADE,

                          title VARCHAR(140) NOT NULL,
                          note TEXT,
                          visited_at TIMESTAMP,

                          lat DOUBLE PRECISION NOT NULL,
                          lon DOUBLE PRECISION NOT NULL,

                          address_label VARCHAR(220),
                          rating INT CHECK (rating IS NULL OR (rating >= 1 AND rating <= 5))


);

-- MEMORY PHOTOS (фотки мест)
CREATE TABLE memory_photos (
                               id BIGSERIAL PRIMARY KEY,
                               memory_id BIGINT NOT NULL REFERENCES memories(id) ON DELETE CASCADE,

                               file_path TEXT NOT NULL


);

-- TAGS
CREATE TABLE tags (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(60) NOT NULL UNIQUE
);

-- MANY-TO-MANY: MEMORY <-> TAG
CREATE TABLE memory_tags (
                             memory_id BIGINT NOT NULL REFERENCES memories(id) ON DELETE CASCADE,
                             tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
                             PRIMARY KEY (memory_id, tag_id)
);

-- Indexes (чтобы быстрее работало)
CREATE INDEX idx_trips_user_id ON trips(user_id);
CREATE INDEX idx_memories_trip_id ON memories(trip_id);
CREATE INDEX idx_memory_photos_memory_id ON memory_photos(memory_id);
