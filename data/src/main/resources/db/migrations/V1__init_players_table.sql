CREATE TABLE IF NOT EXISTS "players" (
    "id" SERIAL PRIMARY KEY,
    "name" varchar,
    "position" varchar,
    "pfrid" varchar UNIQUE
);