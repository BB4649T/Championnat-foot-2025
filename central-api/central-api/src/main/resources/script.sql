CREATE TABLE championship (
                              id VARCHAR(50) PRIMARY KEY,
                              name VARCHAR(100) NOT NULL,
                              country VARCHAR(100) NOT NULL
);

CREATE TABLE club (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      acronym VARCHAR(10) NOT NULL,
                      foundation_year INTEGER NOT NULL,
                      stadium_name VARCHAR(100) NOT NULL,
                      championship_id INTEGER NOT NULL
);

CREATE TABLE player (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        number INTEGER NOT NULL,
                        position VARCHAR(50) NOT NULL,
                        nationality VARCHAR(100) NOT NULL,
                        age INTEGER NOT NULL,
                        club_id INTEGER NOT NULL,
                        goals INTEGER NOT NULL DEFAULT 0,
                        playing_time INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE club_statistics (
                                 id SERIAL PRIMARY KEY,
                                 club_id INTEGER NOT NULL,
                                 club_name VARCHAR(100) NOT NULL,
                                 championship_name VARCHAR(100) NOT NULL,
                                 points INTEGER NOT NULL DEFAULT 0,
                                 goals_scored INTEGER NOT NULL DEFAULT 0,
                                 goals_conceded INTEGER NOT NULL DEFAULT 0,
                                 goal_difference INTEGER NOT NULL DEFAULT 0,
                                 clean_sheets INTEGER NOT NULL DEFAULT 0
);
