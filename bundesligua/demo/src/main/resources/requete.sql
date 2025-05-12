-- Classement de la Ligue 1
SELECT c.nom AS club,
       sc.points,
       sc.buts_marques,
       sc.buts_encaisses,
       sc.difference_buts,
       sc.clean_sheets
FROM statistiques_collectives sc
         JOIN club c ON sc.club_id = c.id
WHERE sc.saison_id = 1 AND c.championnat_id = 1
ORDER BY sc.points DESC, sc.difference_buts DESC;

-- Top 5 buteurs de Ligue 1
SELECT j.nom, j.numero, c.nom AS club, si.buts_marques
FROM joueur j
         JOIN statistiques_individuelles si ON j.id = si.joueur_id
         JOIN club c ON j.club_id = c.id
WHERE si.saison_id = 1 AND c.championnat_id = 1
ORDER BY si.buts_marques DESC
    LIMIT 5;

-- Calendrier des matchs à venir
SELECT m.date_heure,
       c1.nom AS domicile,
       c2.nom AS exterieur,
       m.stade
FROM match m
         JOIN club c1 ON m.club_domicile_id = c1.id
         JOIN club c2 ON m.club_exterieur_id = c2.id
WHERE m.est_joue = FALSE
  AND (c1.championnat_id = 1 OR c2.championnat_id = 1)
ORDER BY m.date_heure;