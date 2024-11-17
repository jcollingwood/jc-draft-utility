CREATE TABLE IF NOT EXISTS "fantasy_league" (
    "id" SERIAL PRIMARY KEY,
    "league_platform" varchar,
    "year" int,
    "league_name" varchar,
    "league_id" varchar,
    "team_id" varchar
);
