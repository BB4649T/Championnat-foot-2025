package com.example.demo.repository;

import com.example.demo.model.StatistiquesIndividuelles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StatistiquesIndividuellesRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StatistiquesIndividuellesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<StatistiquesIndividuelles> rowMapper = (rs, rowNum) ->
            new StatistiquesIndividuelles(
                    rs.getInt("id"),
                    rs.getInt("joueur_id"),
                    rs.getInt("saison_id"),
                    rs.getInt("buts_marques"),
                    rs.getInt("duree_jeu"),
                    rs.getString("joueur_nom"),
                    rs.getString("saison_nom")
            );

    public List<StatistiquesIndividuelles> findAll() {
        return jdbcTemplate.query(
                "SELECT si.*, j.nom as joueur_nom, s.nom as saison_nom " +
                        "FROM statistiques_individuelles si " +
                        "JOIN joueur j ON si.joueur_id = j.id " +
                        "JOIN saison s ON si.saison_id = s.id",
                rowMapper
        );
    }

    public List<StatistiquesIndividuelles> findByJoueurId(Integer joueurId) {
        return jdbcTemplate.query(
                "SELECT si.*, j.nom as joueur_nom, s.nom as saison_nom " +
                        "FROM statistiques_individuelles si " +
                        "JOIN joueur j ON si.joueur_id = j.id " +
                        "JOIN saison s ON si.saison_id = s.id " +
                        "WHERE si.joueur_id = ?",
                rowMapper,
                joueurId
        );
    }

    public Optional<StatistiquesIndividuelles> findByJoueurIdAndSaisonId(Integer joueurId, Integer saisonId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT si.*, j.nom as joueur_nom, s.nom as saison_nom " +
                                    "FROM statistiques_individuelles si " +
                                    "JOIN joueur j ON si.joueur_id = j.id " +
                                    "JOIN saison s ON si.saison_id = s.id " +
                                    "WHERE si.joueur_id = ? AND si.saison_id = ?",
                            rowMapper,
                            joueurId, saisonId
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<StatistiquesIndividuelles> getMeilleursButeursBySaison(Integer saisonId, int limit) {
        return jdbcTemplate.query(
                "SELECT si.*, j.nom as joueur_nom, s.nom as saison_nom " +
                        "FROM statistiques_individuelles si " +
                        "JOIN joueur j ON si.joueur_id = j.id " +
                        "JOIN saison s ON si.saison_id = s.id " +
                        "WHERE si.saison_id = ? " +
                        "ORDER BY si.buts_marques DESC " +
                        "LIMIT ?",
                rowMapper,
                saisonId, limit
        );
    }

    public void initialiserStatistiques(Integer joueurId, Integer saisonId) {
        jdbcTemplate.update(
                "INSERT INTO statistiques_individuelles (joueur_id, saison_id, buts_marques, duree_jeu) " +
                        "VALUES (?, ?, 0, 0) " +
                        "ON CONFLICT (joueur_id, saison_id) DO NOTHING",
                joueurId, saisonId
        );
    }

    public void mettreAJourStatistiquesApresMatch(Integer joueurId, Integer saisonId,
                                                  Integer butsMarques, Integer dureeJeu) {
        jdbcTemplate.update(
                "UPDATE statistiques_individuelles SET " +
                        "buts_marques = buts_marques + ?, " +
                        "duree_jeu = duree_jeu + ? " +
                        "WHERE joueur_id = ? AND saison_id = ?",
                butsMarques, dureeJeu, joueurId, saisonId
        );
    }
    public StatistiquesIndividuelles save(StatistiquesIndividuelles stats) {
        if (stats.getId() == null) {
            Integer id = jdbcTemplate.queryForObject(
                    "INSERT INTO statistiques_individuelles (joueur_id, saison_id, buts_marques, duree_jeu) " +
                            "VALUES (?, ?, ?, ?) RETURNING id",
                    Integer.class,
                    stats.getJoueurId(),
                    stats.getSaisonId(),
                    stats.getButsMarques(),
                    stats.getDureeJeu()
            );
            stats.setId(id);
        } else {
            jdbcTemplate.update(
                    "UPDATE statistiques_individuelles SET " +
                            "joueur_id = ?, saison_id = ?, buts_marques = ?, duree_jeu = ? " +
                            "WHERE id = ?",
                    stats.getJoueurId(),
                    stats.getSaisonId(),
                    stats.getButsMarques(),
                    stats.getDureeJeu(),
                    stats.getId()
            );
        }
        return stats;
    }

}

