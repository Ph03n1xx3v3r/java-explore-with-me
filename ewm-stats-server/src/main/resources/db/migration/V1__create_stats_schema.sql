CREATE TABLE IF NOT EXISTS endpoint_hits (
    id BIGSERIAL PRIMARY KEY,
    app VARCHAR(255) NOT NULL,
    uri VARCHAR(2048) NOT NULL,
    ip VARCHAR(128) NOT NULL,
    hit_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_endpoint_hits_timestamp_uri
    ON endpoint_hits(hit_timestamp, uri);
