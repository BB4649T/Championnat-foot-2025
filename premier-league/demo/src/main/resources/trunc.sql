-- Désactiver temporairement les contraintes de clé étrangère (optionnel, dépend de SGBD, ex: PostgreSQL)
-- SET session_replication_role = replica;

-- Supprimer les données dans l'ordre inverse de dépendance (fils avant parents)
TRUNCATE TABLE statistiques_individuelles RESTART IDENTITY CASCADE;
TRUNCATE TABLE statistiques_collectives RESTART IDENTITY CASCADE;
TRUNCATE TABLE match RESTART IDENTITY CASCADE;
TRUNCATE TABLE saison RESTART IDENTITY CASCADE;
TRUNCATE TABLE joueur RESTART IDENTITY CASCADE;
TRUNCATE TABLE entraineur RESTART IDENTITY CASCADE;
TRUNCATE TABLE club RESTART IDENTITY CASCADE;
TRUNCATE TABLE championnat RESTART IDENTITY CASCADE;

-- Réactiver les contraintes de clé étrangère
-- SET session_replication_role = origin;
