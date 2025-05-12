package com.example.demo.repository;

import com.example.demo.model.Classement;
import com.example.demo.model.StatistiquesCollectives;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StatistiquesCollectivesRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StatistiquesCollectivesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<StatistiquesCollectives> rowMapper = (rs, rowNum) ->
            new StatistiquesCollectives(
                    rs.getInt("id"),
                    rs.getInt("club_id"),
                    rs.getInt("saison_id"),
                    rs.getInt("points"),
                    rs.getInt("buts_marques"),
                    rs.getInt("buts_encaisses"),
                    rs.getInt("difference_buts"),
                    rs.getInt("clean_sheets"),
                    rs.getString("club_nom"),
                    rs.getString("saison_nom")
            );

    private final RowMapper<Classement> classementRowMapper = (rs, rowNum) ->
            new Classement(
                    rs.getInt("rang"),
                    rs.getInt("club_id"),
                    rs.getString("club_nom"),
                    rs.getInt("points"),
                    rs.getInt("buts_marques"),
                    rs.getInt("buts_encaisses"),
                    rs.getInt("difference_buts"),
                    rs.getInt("clean_sheets")
            );

    public List<StatistiquesCollectives> findAll() {
        return jdbcTemplate.query(
                "SELECT sc.*, c.nom as club_nom, s.nom as saison_nom " +
                        "FROM statistiques_collectives sc " +
                        "JOIN club c ON sc.club_id = c.id " +
                        "JOIN saison s ON sc.saison_id = s.id",
                rowMapper
        );
    }

    public List<StatistiquesCollectives> findBySaisonId(Integer saisonId) {
        return jdbcTemplate.query(
                "SELECT sc.*, c.nom as club_nom, s.nom as saison_nom " +
                        "FROM statistiques_collectives sc " +
                        "JOIN club c ON sc.club_id = c.id " +
                        "JOIN saison s ON sc.saison_id = s.id " +
                        "WHERE sc.saison_id = ?",
                rowMapper,
                saisonId
        );
    }

    public Optional<StatistiquesCollectives> findByClubIdAndSaisonId(Integer clubId, Integer saisonId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT sc.*, c.nom as club_nom, s.nom as saison_nom " +
                                    "FROM statistiques_collectives sc " +
                                    "JOIN club c ON sc.club_id = c.id " +
                                    "JOIN saison s ON sc.saison_id = s.id " +
                                    "WHERE sc.club_id = ? AND sc.saison_id = ?",
                            rowMapper,
                            clubId, saisonId
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Classement> getClassementByChampionnatAndSaison(Integer championnatId, Integer saisonId) {
        return jdbcTemplate.query(
                "WITH ranked_stats AS (" +
                        "  SELECT " +
                        "    ROW_NUMBER() OVER (ORDER BY sc.points DESC, sc.difference_buts DESC, sc.clean_sheets DESC) as rang, " +
                        "    sc.club_id, c.nom as club_nom, sc.points, sc.buts_marques, sc.buts_encaisses, " +
                        "    sc.difference_buts, sc.clean_sheets " +
                        "  FROM statistiques_collectives sc " +
                        "  JOIN club c ON sc.club_id = c.id " +
                        "  WHERE c.championnat_id = ? AND sc.saison_id = ? " +
                        ") " +
                        "SELECT * FROM ranked_stats",
                classementRowMapper,
                championnatId, saisonId
        );
    }

    public void initialiserStatistiques(Integer clubId, Integer saisonId) {
        jdbcTemplate.update(
                "INSERT INTO statistiques_collectives (club_id, saison_id, points, buts_marques, " +
                        "buts_encaisses, difference_buts, clean_sheets) " +
                        "VALUES (?, ?, 0, 0, 0, 0, 0) " +
                        "ON CONFLICT (club_id, saison_id) DO NOTHING",
                clubId, saisonId
        );
    }

    public void mettreAJourStatistiquesApresMatch(Integer clubId, Integer saisonId,
                                                  Integer pointsGagnes, Integer butsMarques,
                                                  Integer butsEncaisses, boolean cleanSheet) {
        jdbcTemplate.update(
                "UPDATE statistiques_collectives SET " +
                        "points = points + ?, " +
                        "buts_marques = buts_marques + ?, " +
                        "buts_encaisses = buts_encaisses + ?, " +
                        "difference_buts = buts_marques - buts_encaisses, " +
                        "clean_sheets = clean_sheets + ? " +
                        "WHERE club_id = ? AND saison_id = ?",
                pointsGagnes, butsMarques, butsEncaisses, cleanSheet ? 1 : 0, clubId, saisonId
        );
    }
    public StatistiquesCollectives save(StatistiquesCollectives stats) {
        if (stats.getId() == null) {
            Integer id = jdbcTemplate.queryForObject(
                    "INSERT INTO statistiques_collectives (club_id, saison_id, points, buts_marques, buts_encaisses, difference_buts, clean_sheets) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id",
                    Integer.class,
                    stats.getClubId(),
                    stats.getSaisonId(),
                    stats.getPoints(),
                    stats.getButsMarques(),
                    stats.getButsEncaisses(),
                    stats.getDifferenceButs(),
                    stats.getCleanSheets()
            );
            stats.setId(id);
        } else {
            jdbcTemplate.update(
                    "UPDATE statistiques_collectives SET " +
                            "club_id = ?, saison_id = ?, points = ?, buts_marques = ?, " +
                            "buts_encaisses = ?, difference_buts = ?, clean_sheets = ? " +
                            "WHERE id = ?",
                    stats.getClubId(),
                    stats.getSaisonId(),
                    stats.getPoints(),
                    stats.getButsMarques(),
                    stats.getButsEncaisses(),
                    stats.getDifferenceButs(),
                    stats.getCleanSheets(),
                    stats.getId()
            );
        }
        return stats;
    }
}

