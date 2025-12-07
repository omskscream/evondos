CREATE TABLE IF NOT EXISTS measurement(
    id BIGSERIAL,
    patient_id BIGINT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    sys SMALLINT DEFAULT 0,
    dia SMALLINT DEFAULT 0,
    pulse SMALLINT DEFAULT 0
);

ALTER TABLE measurement
ADD CONSTRAINT measurement_pkey
PRIMARY KEY (patient_id, timestamp);