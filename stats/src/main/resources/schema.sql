DROP TABLE IF EXISTS endpoint_hit, view_stats;

CREATE TABLE IF NOT EXISTS endpoint_hit(
 id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
 app VARCHAR(200) NOT NULL,
 uri VARCHAR(500) NOT NULL,
 ip VARCHAR(20) NOT NUll,
 time_stamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
 CONSTRAINT hit_id PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS view_stats(
 app VARCHAR(200) NOT NULL,
 uri VARCHAR(500) NOT NULL,
 hits BIGINT,
 CONSTRAINT app_name PRIMARY KEY(app)
);