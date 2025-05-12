-- Insert Championnat
INSERT INTO championnat (nom, pays) VALUES ('Ligue 1', 'France');

-- Insert Saison
INSERT INTO saison (nom, est_terminee) VALUES ('2024', FALSE);

-- Insert Clubs
INSERT INTO club (nom, acronyme, annee_creation, nom_stade, championnat_id) VALUES
                                                                                ('Club 4', 'C4', 1902, 'Stade 4', 1),
                                                                                ('Club 5', 'C5', 1905, 'Stade 5', 1),
                                                                                ('Club 6', 'C6', 1910, 'Stade 6', 1);

-- Insert Entraineurs
INSERT INTO entraineur (nom, nationalite, club_id) VALUES
                                                       ('Entraîneur 4', 'Malgache', 1),
                                                       ('Entraîneur 5', 'Ivorien', 2),
                                                       ('Entraîneur 6', 'Espagnol', 3);

-- Insert Joueurs for Club 4
INSERT INTO joueur (nom, numero, poste, nationalite, age, club_id) VALUES
                                                                       ('Gardien 4', 1, 'GOAL_KEEPER', 'Brésilien', 30, 1),
                                                                       ('Défense 4', 2, 'DEFENSE', 'Brésilien', 25, 1),
                                                                       ('Milieu 4', 5, 'MIDFIELDER', 'Français', 24, 1),
                                                                       ('Attaquant 4', 7, 'STRIKER', 'Allemand', 17, 1);

-- Insert Joueurs for Club 5
INSERT INTO joueur (nom, numero, poste, nationalite, age, club_id) VALUES
                                                                       ('Gardien 5', 1, 'GOAL_KEEPER', 'Français', 21, 2),
                                                                       ('Défense 5', 2, 'DEFENSE', 'Belge', 34, 2),
                                                                       ('Milieu 5', 5, 'MIDFIELDER', 'Français', 29, 2),
                                                                       ('Attaquant 5', 7, 'STRIKER', 'Allemand', 18, 2);

-- Insert Joueurs for Club 6
INSERT INTO joueur (nom, numero, poste, nationalite, age, club_id) VALUES
                                                                       ('Gardien 6', 1, 'GOAL_KEEPER', 'Espagnol', 28, 3),
                                                                       ('Défense 6', 2, 'DEFENSE', 'Brésilien', 21, 3),
                                                                       ('Milieu 6', 5, 'MIDFIELDER', 'Italien', 29, 3),
                                                                       ('Attaquant 6', 7, 'STRIKER', 'Allemand', 23, 3);

-- Insert Matchs
INSERT INTO match (club_domicile_id, club_exterieur_id, stade, date_heure, score_domicile, score_exterieur, saison_id, est_joue) VALUES
                                                                                                                                     (1, 2, 'Stade 4', NULL, 4, 1, 1, TRUE),  -- Club 4 vs Club 5
                                                                                                                                     (2, 3, 'Stade 5', NULL, 1, 1, 1, TRUE),  -- Club 5 vs Club 6
                                                                                                                                     (1, 3, 'Stade 4', NULL, 1, 0, 1, TRUE),  -- Club 4 vs Club 6
                                                                                                                                     (3, 2, 'Stade 6', NULL, 0, 0, 1, FALSE), -- Club 6 vs Club 5 (not started)
                                                                                                                                     (2, 1, 'Stade 5', NULL, 1, 0, 1, FALSE), -- Club 5 vs Club 4 (not started)
                                                                                                                                     (3, 1, 'Stade 6', NULL, 3, 2, 1, TRUE);  -- Club 6 vs Club 4

-- Insert Statistiques Collectives
INSERT INTO statistiques_collectives (club_id, saison_id, points, buts_marques, buts_encaisses, difference_buts, clean_sheets) VALUES
                                                                                                                                   (1, 1, 6, 7, 5, 2, 0),  -- Club 4
                                                                                                                                   (2, 1, 5, 4, 5, -1, 1), -- Club 5
                                                                                                                                   (3, 1, 5, 5, 5, 0, 1);  -- Club 6

-- Insert Statistiques Individuelles
-- Club 4 players
INSERT INTO statistiques_individuelles (joueur_id, saison_id, buts_marques, duree_jeu) VALUES
                                                                                           (1, 1, 0, 32400),  -- Gardien 4 (own goals not counted here)
                                                                                           (2, 1, 1, 32400),  -- Défense 4
                                                                                           (3, 1, 2, 31400),  -- Milieu 4
                                                                                           (4, 1, 4, 32400);  -- Attaquant 4

-- Club 5 players
INSERT INTO statistiques_individuelles (joueur_id, saison_id, buts_marques, duree_jeu) VALUES
                                                                                           (5, 1, 0, 32400),  -- Gardien 5
                                                                                           (6, 1, 0, 32400),  -- Défense 5
                                                                                           (7, 1, 0, 32400),  -- Milieu 5
                                                                                           (8, 1, 1, 32400);  -- Attaquant 5

-- Club 6 players
INSERT INTO statistiques_individuelles (joueur_id, saison_id, buts_marques, duree_jeu) VALUES
                                                                                           (9, 1, 0, 32400),  -- Gardien 6
                                                                                           (10, 1, 0, 32400), -- Défense 6
                                                                                           (11, 1, 0, 32400), -- Milieu 6
                                                                                           (12, 1, 1, 32400);  -- Attaquant 6 (only 3240 seconds as per data)