DROP TABLE IF EXISTS app_users, category, location;

CREATE TABLE IF NOT EXISTS app_users(
 id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
 email VARCHAR(254) UNIQUE NOT NULL,
 name VARCHAR(250) NOT NULL,
 CONSTRAINT user_id PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS category(
 id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
 name VARCHAR(50) UNIQUE NOT NULL,
 CONSTRAINT category_id PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS location(
 id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
 lat FLOAT NOT NULL,
 lon FLOAT NOT NULL,
 CONSTRAINT location_id PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS events(
 id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
 annotation VARCHAR(2000) NOT NULL,
 category_id BIGINT NOT NULL REFERENCES category(id) ON DELETE RESTRICT,
 confirmed_requests BIGINT,
 description VARCHAR(7000),
 event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
 initiator_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE RESTRICT,
 location_id BIGINT NOT NULL REFERENCES location(id) ON DELETE CASCADE,
 paid BOOLEAN NOT NULL,
 participant_limit BIGINT DEFAULT 0,
 created_on TIMESTAMP WITHOUT TIME ZONE,
 published_on TIMESTAMP WITHOUT TIME ZONE,
 request_moderation BOOLEAN DEFAULT TRUE,
 state VARCHAR(100),
 title VARCHAR(120) NOT NULL,
 views BIGINT DEFAULT 0,
 CONSTRAINT event_id PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS requests(
);