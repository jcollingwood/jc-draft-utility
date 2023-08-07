CREATE TABLE IF NOT EXISTS "playergamestats" (
    "id" SERIAL PRIMARY KEY,
    "playerid" int,
    "year" int,
    "week" int,
    "team" varchar,
    "opponent" varchar,
    -- pass stats
    "passcomps" int,
    "passatts" int,
    "passyds" int,
    "passtds" int,
    "ints" int,
    "sacks" int,
    -- rush stats
    "rushatts" int,
    "rushyds" int,
    "rushtds" int,
    -- receiving stats
    "tgts" int,
    "recs" int,
    "recyds" int,
    "rectds" int,
    UNIQUE (playerid, year, week),
    FOREIGN KEY (playerid) REFERENCES players(id)
);