CREATE TABLE IF NOT EXISTS "cached_data" (
    "id" SERIAL PRIMARY KEY,
    "data_type" varchar,
    "timestamp" timestamp,
    "data_key" varchar,
    "data" bytea
);
