-- Drop tables if they exist (for development purpose)
DROP TABLE IF EXISTS statistiques_individuelles;
DROP TABLE IF EXISTS statistiques_collectives;
DROP TABLE IF EXISTS match;
DROP TABLE IF EXISTS saison;
DROP TABLE IF EXISTS joueur;
DROP TABLE IF EXISTS entraineur;
DROP TABLE IF EXISTS club;
DROP TABLE IF EXISTS championnat;

-- Create tables
CREATE TABLE championnat (
                             id SERIAL PRIMARY KEY,
                             nom VARCHAR(100) NOT NULL,
                             pays VARCHAR(100) NOT NULL
);

CREATE TABLE club (
                      id SERIAL PRIMARY KEY,
                      nom VARCHAR(100) UNIQUE NOT NULL,
                      acronyme VARCHAR(5) NOT NULL,
                      annee_creation INTEGER NOT NULL,
                      nom_stade VARCHAR(100) NOT NULL,
                      championnat_id INTEGER NOT NULL,
                      FOREIGN KEY (championnat_id) REFERENCES championnat(id)
);

CREATE TABLE entraineur (
                            id SERIAL PRIMARY KEY,
                            nom VARCHAR(100) NOT NULL,
                            nationalite VARCHAR(100) NOT NULL,
                            club_id INTEGER NOT NULL,
                            FOREIGN KEY (club_id) REFERENCES club(id)
);

CREATE TABLE joueur (
                        id SERIAL PRIMARY KEY,
                        nom VARCHAR(100) NOT NULL,
                        numero INTEGER NOT NULL,
                        poste VARCHAR(20) NOT NULL,
                        nationalite VARCHAR(100) NOT NULL,
                        age INTEGER NOT NULL,
                        club_id INTEGER NOT NULL,
                        FOREIGN KEY (club_id) REFERENCES club(id),
                        UNIQUE (club_id, numero)
);

CREATE TABLE saison (
                        id SERIAL PRIMARY KEY,
                        nom VARCHAR(20) NOT NULL,
                        est_terminee BOOLEAN DEFAULT FALSE
);

CREATE TABLE match (
                       id SERIAL PRIMARY KEY,
                       club_domicile_id INTEGER NOT NULL,
                       club_exterieur_id INTEGER NOT NULL,
                       stade VARCHAR(100) NOT NULL,
                       date_heure TIMESTAMP NULL,  -- Changé en NULL explicitement
                       score_domicile INTEGER,
                       score_exterieur INTEGER,
                       saison_id INTEGER NOT NULL,
                       est_joue BOOLEAN DEFAULT FALSE,
                       FOREIGN KEY (club_domicile_id) REFERENCES club(id),
                       FOREIGN KEY (club_exterieur_id) REFERENCES club(id),
                       FOREIGN KEY (saison_id) REFERENCES saison(id)
);

CREATE TABLE statistiques_collectives (
                                          id SERIAL PRIMARY KEY,
                                          club_id INTEGER NOT NULL,
                                          saison_id INTEGER NOT NULL,
                                          points INTEGER DEFAULT 0,
                                          buts_marques INTEGER DEFAULT 0,
                                          buts_encaisses INTEGER DEFAULT 0,
                                          difference_buts INTEGER DEFAULT 0,
                                          clean_sheets INTEGER DEFAULT 0,
                                          FOREIGN KEY (club_id) REFERENCES club(id),
                                          FOREIGN KEY (saison_id) REFERENCES saison(id),
                                          UNIQUE (club_id, saison_id)
);

CREATE TABLE statistiques_individuelles (
                                            id SERIAL PRIMARY KEY,
                                            joueur_id INTEGER NOT NULL,
                                            saison_id INTEGER NOT NULL,
                                            buts_marques INTEGER DEFAULT 0,
                                            duree_jeu INTEGER DEFAULT 0, -- en minutes
                                            FOREIGN KEY (joueur_id) REFERENCES joueur(id),
                                            FOREIGN KEY (saison_id) REFERENCES saison(id),
                                            UNIQUE (joueur_id, saison_id)
);
