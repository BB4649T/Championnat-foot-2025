INSERT INTO championnat (nom, pays) VALUES
    ('Ligue 1', 'France');

INSERT INTO club (nom, acronyme, annee_creation, nom_stade, championnat_id) VALUES
                                                                                ('Club 1', 'C1', 1902, 'Stade 1', 1),
                                                                                ('Club 2', 'C2', 1905, 'Stade 2', 1),
                                                                                ('Club 3', 'C3', 1910, 'Stade 3', 1);

INSERT INTO entraineur (nom, nationalite, club_id) VALUES
                                                       ('Entraîneur 1', 'Français', 1),
                                                       ('Entraîneur 2', 'Italien', 2),
                                                       ('Entraîneur 3', 'Allemand', 3);

-- Club 1 Players
INSERT INTO joueur (nom, numero, poste, nationalite, age, club_id) VALUES
                                                                       ('Gardien 1', 1, 'GOAL_KEEPER', 'Espagnol', 30, 1),
                                                                       ('Défense 1', 2, 'DEFENSE', 'Espagnol', 25, 1),
                                                                       ('Milieu 1', 5, 'MIDFIELDER', 'Espagnol', 24, 1),
                                                                       ('Attaquant 1', 7, 'STRIKER', 'Espagnol', 17, 1);

-- Club 2 Players
INSERT INTO joueur (nom, numero, poste, nationalite, age, club_id) VALUES
                                                                       ('Gardien 2', 1, 'GOAL_KEEPER', 'Espagnol', 21, 2),
                                                                       ('Défense 2', 2, 'DEFENSE', 'Belge', 34, 2),
                                                                       ('Milieu 2', 5, 'MIDFIELDER', 'Français', 29, 2),
                                                                       ('Attaquant 2', 7, 'STRIKER', 'Allemand', 18, 2);

-- Club 3 Players
INSERT INTO joueur (nom, numero, poste, nationalite, age, club_id) VALUES
                                                                       ('Gardien 3', 1, 'GOAL_KEEPER', 'Brésilien', 28, 3),
                                                                       ('Défense 3', 2, 'DEFENSE', 'Brésilien', 21, 3),
                                                                       ('Milieu 3', 5, 'MIDFIELDER', 'Français', 29, 3),
                                                                       ('Attaquant 3', 7, 'STRIKER', 'Allemand', 23, 3);


INSERT INTO saison (nom, est_terminee) VALUES
                                           ('2024', FALSE),
                                           ('2025', FALSE);

INSERT INTO match (club_domicile_id, club_exterieur_id, stade, date_heure, score_domicile, score_exterieur, saison_id, est_joue) VALUES
                                                                                                                                     (1, 2, 'Stade 1', NULL, NULL, NULL, 1, FALSE),
                                                                                                                                     (2, 3, 'Stade 2', NULL, NULL, NULL, 1, FALSE),
                                                                                                                                     (1, 3, 'Stade 1', NULL, NULL, NULL, 1, FALSE),
                                                                                                                                     (3, 2, 'Stade 3', NULL, NULL, NULL, 1, FALSE),
                                                                                                                                     (2, 1, 'Stade 2', NULL, NULL, NULL, 1, FALSE),
                                                                                                                                     (3, 1, 'Stade 3', NULL, NULL, NULL, 1, FALSE);

INSERT INTO match (club_domicile_id, club_exterieur_id, stade, date_heure, score_domicile, score_exterieur, saison_id, est_joue) VALUES
                                                                                                                                     (1, 2, 'Stade 1', NULL, NULL, NULL, 2, FALSE),
                                                                                                                                     (2, 3, 'Stade 2', NULL, NULL, NULL, 2, FALSE),
                                                                                                                                     (1, 3, 'Stade 1', NULL, NULL, NULL, 2, FALSE),
                                                                                                                                     (3, 2, 'Stade 3', NULL, NULL, NULL, 2, FALSE),
                                                                                                                                     (2, 1, 'Stade 2', NULL, NULL, NULL, 2, FALSE),
                                                                                                                                     (3, 1, 'Stade 3', NULL, NULL, NULL, 2, FALSE);

-- For Season 2024
INSERT INTO statistiques_collectives (club_id, saison_id) VALUES
                                                              (1, 1), (2, 1), (3, 1);

-- For Season 2025
INSERT INTO statistiques_collectives (club_id, saison_id) VALUES
                                                              (1, 2), (2, 2), (3, 2);


-- For Season 2024
INSERT INTO statistiques_individuelles (joueur_id, saison_id) VALUES
                                                                  (1, 1), (2, 1), (3, 1), (4, 1),  -- Club 1 players
                                                                  (5, 1), (6, 1), (7, 1), (8, 1),    -- Club 2 players
                                                                  (9, 1), (10, 1), (11, 1), (12, 1); -- Club 3 players

-- For Season 2025
INSERT INTO statistiques_individuelles (joueur_id, saison_id) VALUES
                                                                  (1, 2), (2, 2), (3, 2), (4, 2),  -- Club 1 players
                                                                  (5, 2), (6, 2), (7, 2), (8, 2),    -- Club 2 players
                                                                  (9, 2), (10, 2), (11, 2), (12, 2); -- Club 3 players