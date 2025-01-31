-- Создание пользовательского типа player_names
DO $$ BEGIN
    CREATE TYPE player_names AS ENUM (
        'DESMOND',
        'BLACK_VISION',
        'GLOXINIA',
        'B4ONE',
        'NEKIT',
        'KOPFIRE',
        'MVFOREVER01',
        'WOLF_SMXL'
    );
EXCEPTION WHEN duplicate_object THEN null; -- Игнорировать, если тип уже существует
END $$;

-- Создание пользовательского типа match_types
DO $$ BEGIN
    CREATE TYPE match_types AS ENUM (
        'MATCH_MAKING',
        'WINGMAN',
        'PREMIER',
        'FACEIT'
    );
EXCEPTION WHEN duplicate_object THEN null; -- Игнорировать, если тип уже существует
END $$;

CREATE TABLE matches (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    player_name player_names NOT NULL,
    date TIMESTAMP,
    rating DOUBLE PRECISION,
    smoke_kill INTEGER,
    open_kill INTEGER,
    three_kill INTEGER,
    four_kill INTEGER,
    ace INTEGER,
    flash INTEGER,
    trade INTEGER,
    wall_bang INTEGER,
    clutch_one INTEGER,
    clutch_two INTEGER,
    clutch_three INTEGER,
    clutch_four INTEGER,
    clutch_five INTEGER,
    type match_types NOT NULL
);

CREATE TABLE tops (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    player_name player_names NOT NULL,
    year INTEGER,
    rating DOUBLE PRECISION,
    place INTEGER
);

-- Добавление индекса для поля rating в таблице matches
CREATE INDEX idx_matches_rating ON matches (rating);

-- Добавление индекса для поля place в таблице tops
CREATE INDEX idx_tops_place ON tops (place);
