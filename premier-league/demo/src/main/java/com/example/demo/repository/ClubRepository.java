package com.example.demo.repository;

import com.example.demo.model.Club;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClubRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClubRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Club> rowMapper = (rs, rowNum) ->
            new Club(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("acronyme"),
                    rs.getInt("annee_creation"),
                    rs.getString("nom_stade"),
                    rs.getInt("championnat_id"),
                    rs.getString("championnat_nom")
            );

    public List<Club> findAll() {
        return jdbcTemplate.query(
                "SELECT c.*, ch.nom as championnat_nom FROM club c " +
                        "JOIN championnat ch ON c.championnat_id = ch.id",
                rowMapper
        );
    }

    public List<Club> findByChampionnatId(Integer championnatId) {
        return jdbcTemplate.query(
                "SELECT c.*, ch.nom as championnat_nom FROM club c " +
                        "JOIN championnat ch ON c.championnat_id = ch.id " +
                        "WHERE c.championnat_id = ?",
                rowMapper,
                championnatId
        );
    }

    public Optional<Club> findById(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT c.*, ch.nom as championnat_nom FROM club c " +
                                    "JOIN championnat ch ON c.championnat_id = ch.id " +
                                    "WHERE c.id = ?",
                            rowMapper,
                            id
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Club save(Club club) {
        if (club.getId() == null) {
            Integer id = jdbcTemplate.queryForObject(
                    "INSERT INTO club (nom, acronyme, annee_creation, nom_stade, championnat_id) " +
                            "VALUES (?, ?, ?, ?, ?) RETURNING id",
                    Integer.class,
                    club.getNom(),
                    club.getAcronyme(),
                    club.getAnneeCreation(),
                    club.getNomStade(),
                    club.getChampionnatId()
            );
            club.setId(id);
        } else {
            jdbcTemplate.update(
                    "UPDATE club SET nom = ?, acronyme = ?, annee_creation = ?, " +
                            "nom_stade = ?, championnat_id = ? WHERE id = ?",
                    club.getNom(),
                    club.getAcronyme(),
                    club.getAnneeCreation(),
                    club.getNomStade(),
                    club.getChampionnatId(),
                    club.getId()
            );
        }
        return club;
    }

    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM club WHERE id = ?", id);
    }
}

