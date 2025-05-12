package com.example.demo.repository;

import com.example.demo.model.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class MatchRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Match> rowMapper = (rs, rowNum) -> {
        Match match = new Match();
        match.setId(rs.getInt("id"));
        match.setClubDomicileId(rs.getInt("club_domicile_id"));
        match.setClubExterieurId(rs.getInt("club_exterieur_id"));
        match.setStade(rs.getString("stade"));

        // Gestion appropriée de la valeur null
        Timestamp dateHeure = rs.getTimestamp("date_heure");
        match.setDateHeure(dateHeure != null ? dateHeure.toLocalDateTime() : null);

        match.setScoreDomicile(rs.getObject("score_domicile", Integer.class));
        match.setScoreExterieur(rs.getObject("score_exterieur", Integer.class));
        match.setSaisonId(rs.getInt("saison_id"));
        match.setEstJoue(rs.getBoolean("est_joue"));

        // Colonnes supplémentaires pour l'affichage
        if (rs.getMetaData().getColumnCount() > 9) {
            match.setClubDomicileNom(rs.getString("club_domicile_nom"));
            match.setClubExterieurNom(rs.getString("club_exterieur_nom"));
            match.setSaisonNom(rs.getString("saison_nom"));
        }

        return match;
    };


    public List<Match> findAll() {
        return jdbcTemplate.query(
                "SELECT m.*, cd.nom as club_domicile_nom, ce.nom as club_exterieur_nom, s.nom as saison_nom " +
                        "FROM match m " +
                        "JOIN club cd ON m.club_domicile_id = cd.id " +
                        "JOIN club ce ON m.club_exterieur_id = ce.id " +
                        "JOIN saison s ON m.saison_id = s.id",
                rowMapper
        );
    }

    public List<Match> findBySaisonId(Integer saisonId) {
        return jdbcTemplate.query(
                "SELECT m.*, cd.nom as club_domicile_nom, ce.nom as club_exterieur_nom, s.nom as saison_nom " +
                        "FROM match m " +
                        "JOIN club cd ON m.club_domicile_id = cd.id " +
                        "JOIN club ce ON m.club_exterieur_id = ce.id " +
                        "JOIN saison s ON m.saison_id = s.id " +
                        "WHERE m.saison_id = ?",
                rowMapper,
                saisonId
        );
    }

    public List<Match> findByClubIdAndSaisonId(Integer clubId, Integer saisonId) {
        return jdbcTemplate.query(
                "SELECT m.*, cd.nom as club_domicile_nom, ce.nom as club_exterieur_nom, s.nom as saison_nom " +
                        "FROM match m " +
                        "JOIN club cd ON m.club_domicile_id = cd.id " +
                        "JOIN club ce ON m.club_exterieur_id = ce.id " +
                        "JOIN saison s ON m.saison_id = s.id " +
                        "WHERE (m.club_domicile_id = ? OR m.club_exterieur_id = ?) AND m.saison_id = ?",
                rowMapper,
                clubId, clubId, saisonId
        );
    }

    public Optional<Match> findById(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT m.*, cd.nom as club_domicile_nom, ce.nom as club_exterieur_nom, s.nom as saison_nom " +
                                    "FROM match m " +
                                    "JOIN club cd ON m.club_domicile_id = cd.id " +
                                    "JOIN club ce ON m.club_exterieur_id = ce.id " +
                                    "JOIN saison s ON m.saison_id = s.id " +
                                    "WHERE m.id = ?",
                            rowMapper,
                            id
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Match save(Match match) {
        if (match.getId() == null) {
            Integer id = jdbcTemplate.queryForObject(
                    "INSERT INTO match (club_domicile_id, club_exterieur_id, stade, date_heure, " +
                            "score_domicile, score_exterieur, saison_id, est_joue) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id",
                    Integer.class,
                    match.getClubDomicileId(),
                    match.getClubExterieurId(),
                    match.getStade(),
                    match.getDateHeure() != null ? Timestamp.valueOf(match.getDateHeure()) : null,
                    match.getScoreDomicile(),
                    match.getScoreExterieur(),
                    match.getSaisonId(),
                    match.isEstJoue()
            );
            match.setId(id);
        } else {
            jdbcTemplate.update(
                    "UPDATE match SET club_domicile_id = ?, club_exterieur_id = ?, stade = ?, " +
                            "date_heure = ?, score_domicile = ?, score_exterieur = ?, saison_id = ?, est_joue = ? " +
                            "WHERE id = ?",
                    match.getClubDomicileId(),
                    match.getClubExterieurId(),
                    match.getStade(),
                    match.getDateHeure() != null ? Timestamp.valueOf(match.getDateHeure()) : null,
                    match.getScoreDomicile(),
                    match.getScoreExterieur(),
                    match.getSaisonId(),
                    match.isEstJoue(),
                    match.getId()
            );
        }
        return match;
    }
    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM match WHERE id = ?", id);
    }
}

