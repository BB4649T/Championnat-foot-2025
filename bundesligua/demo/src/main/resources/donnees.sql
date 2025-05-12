-- Insertion des championnats
INSERT INTO championnat (nom, pays) VALUES ('Ligue 1', 'France');

-- Insertion des clubs
INSERT INTO club (nom, acronyme, annee_creation, nom_stade, championnat_id)
VALUES
    ('Club 1', 'C1', 1902, 'Stade 1', 1),
    ('Club 2', 'C2', 1905, 'Stade 2', 1),
    ('Club 3', 'C3', 1910, 'Stade 3', 1);

-- Insertion des entraîneurs
INSERT INTO entraineur (nom, nationalite, club_id)
VALUES
    ('Entraîneur 1', 'Français', 1),
    ('Entraîneur 2', 'Italien', 2),
    ('Entraîneur 3', 'Allemand', 3);

-- Insertion des joueurs
INSERT INTO joueur (nom, numero, poste, nationalite, age, club_id)
VALUES
-- Club 1
('Gardien 1', 1, 'GOAL_KEEPER', 'Espagnol', 30, 1),
('Défense 1', 2, 'DEFENSE', 'Espagnol', 25, 1),
('Milieu 1', 5, 'MIDFIELDER', 'Espagnol', 24, 1),
('Attaquant 1', 7, 'STRIKER', 'Espagnol', 17, 1),
-- Club 2
('Gardien 2', 1, 'GOAL_KEEPER', 'Espagnol', 21, 2),
('Défense 2', 2, 'DEFENSE', 'Belge', 34, 2),
('Milieu 2', 5, 'MIDFIELDER', 'Français', 29, 2),
('Attaquant 2', 7, 'STRIKER', 'Allemand', 18, 2),
-- Club 3
('Gardien 3', 1, 'GOAL_KEEPER', 'Brésilien', 28, 3),
('Défense 3', 2, 'DEFENSE', 'Brésilien', 21, 3),
('Milieu 3', 5, 'MIDFIELDER', 'Français', 29, 3),
('Attaquant 3', 7, 'STRIKER', 'Allemand', 23, 3);

-- Insertion de la saison
INSERT INTO saison (nom, est_terminee) VALUES ('2024', FALSE);

-- Insertion des matchs
INSERT INTO match (club_domicile_id, club_exterieur_id, stade, date_heure, score_domicile, score_exterieur, saison_id, est_joue)
VALUES
-- Match 1: Club 1 vs Club 2 - FINISHED (4-1)
(1, 2, 'Stade 1', NOW(), 4, 1, 1, TRUE),
-- Match 2: Club 2 vs Club 3 - FINISHED (0-1)
(2, 3, 'Stade 2', NOW(), 0, 1, 1, TRUE),
-- Match 3: Club 1 vs Club 3 - FINISHED (1-0)
(1, 3, 'Stade 1', NOW(), 1, 0, 1, TRUE),
-- Match 4: Club 3 vs Club 2 - NOT_STARTED (0-0)
(3, 2, 'Stade 3', NULL, NULL, NULL, 1, FALSE),
-- Match 5: Club 2 vs Club 1 - FINISHED (1-0)
(2, 1, 'Stade 2', NOW(), 1, 0, 1, TRUE),
-- Match 6: Club 3 vs Club 1 - FINISHED (3-2)
(3, 1, 'Stade 3', NOW(), 3, 2, 1, TRUE);

-- Insertion des statistiques collectives
INSERT INTO statistiques_collectives (club_id, saison_id, points, buts_marques, buts_encaisses, difference_buts, clean_sheets)
VALUES
-- Club 1: 2 victoires (6 pts), 7 buts marqués, 5 buts encaissés, +2 différence, 1 clean sheet
(1, 1, 6, 7, 5, 2, 1),
-- Club 2: 1 victoire (3 pts), 1 but marqué, 5 buts encaissés, -4 différence, 0 clean sheet
(2, 1, 4, 1, 5, -4, 0),
-- Club 3: 2 victoires (6 pts), 4 buts marqués, 3 buts encaissés, +1 différence, 1 clean sheet
(3, 1, 7, 4, 3, 1, 1);

-- Insertion des statistiques individuelles
INSERT INTO statistiques_individuelles (joueur_id, saison_id, buts_marques, duree_jeu)
VALUES
-- Joueurs Club 1
(1, 1, 0, 32400), -- Gardien 1 (a marqué 3 own goals mais ce n'est pas compté ici)
(2, 1, 1, 32400), -- Défense 1
(3, 1, 1, 32400), -- Milieu 1
(4, 1, 3, 33000), -- Attaquant 1
-- Joueurs Club 2
(5, 1, 0, 32400), -- Gardien 2
(6, 1, 0, 32400), -- Défense 2
(7, 1, 0, 32400), -- Milieu 2
(8, 1, 1, 32400), -- Attaquant 2
-- Joueurs Club 3
(9, 1, 0, 32400), -- Gardien 3
(10, 1, 0, 32400), -- Défense 3
(11, 1, 0, 32400), -- Milieu 3
(12, 1, 1, 32400); -- Attaquant 3