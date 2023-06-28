CREATE TABLE IF NOT EXISTS endpoint_hit
(
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    app     VARCHAR(255)             NOT NULL,
    uri     VARCHAR(255)             NOT NULL,
    user_ip VARCHAR(255)             NOT NULL,
    created TIMESTAMP WITH TIME ZONE NOT NULL

);