package com.example.demo.repository;

import com.example.demo.model.Entraineur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EntraineurRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EntraineurRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Entraineur> rowMapper = (rs, rowNum) ->
            new Entraineur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("nationalite"),
                    rs.getInt("club_id"),
                    rs.getString("club_nom")
            );

    public List<Entraineur> findAll() {
        return jdbcTemplate.query(
                "SELECT e.*, c.nom as club_nom FROM entraineur e " +
                        "JOIN club c ON e.club_id = c.id",
                rowMapper
        );
    }

    public Optional<Entraineur> findById(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT e.*, c.nom as club_nom FROM entraineur e " +
                                    "JOIN club c ON e.club_id = c.id " +
                                    "WHERE e.id = ?",
                            rowMapper,
                            id
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Entraineur> findByClubId(Integer clubId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT e.*, c.nom as club_nom FROM entraineur e " +
                                    "JOIN club c ON e.club_id = c.id " +
                                    "WHERE e.club_id = ?",
                            rowMapper,
                            clubId
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Entraineur save(Entraineur entraineur) {
        if (entraineur.getId() == null) {
            Integer id = jdbcTemplate.queryForObject(
                    "INSERT INTO entraineur (nom, nationalite, club_id) " +
                            "VALUES (?, ?, ?) RETURNING id",
                    Integer.class,
                    entraineur.getNom(),
                    entraineur.getNationalite(),
                    entraineur.getClubId()
            );
            entraineur.setId(id);
        } else {
            jdbcTemplate.update(
                    "UPDATE entraineur SET nom = ?, nationalite = ?, club_id = ? " +
                            "WHERE id = ?",
                    entraineur.getNom(),
                    entraineur.getNationalite(),
                    entraineur.getClubId(),
                    entraineur.getId()
            );
        }
        return entraineur;
    }

    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM entraineur WHERE id = ?", id);
    }
    public List<Entraineur> findByNationalite(String nationalite) {
        return jdbcTemplate.query(
                "SELECT e.*, c.nom as club_nom FROM entraineur e " +
                        "JOIN club c ON e.club_id = c.id " +
                        "WHERE e.nationalite = ?",
                rowMapper,
                nationalite
        );
    }

}

