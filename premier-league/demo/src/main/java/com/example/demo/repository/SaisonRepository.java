package com.example.demo.repository;

import com.example.demo.model.Saison;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SaisonRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SaisonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Saison> rowMapper = (rs, rowNum) ->
            new Saison(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getBoolean("est_terminee")
            );

    public List<Saison> findAll() {
        return jdbcTemplate.query("SELECT * FROM saison", rowMapper);
    }

    public Optional<Saison> findById(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM saison WHERE id = ?",
                            rowMapper,
                            id
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Saison> findCurrentSaison() {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM saison WHERE est_terminee = false ORDER BY id DESC LIMIT 1",
                            rowMapper
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Saison save(Saison saison) {
        if (saison.getId() == null) {
            Integer id = jdbcTemplate.queryForObject(
                    "INSERT INTO saison (nom, est_terminee) VALUES (?, ?) RETURNING id",
                    Integer.class,
                    saison.getNom(),
                    saison.isEstTerminee()
            );
            saison.setId(id);
        } else {
            jdbcTemplate.update(
                    "UPDATE saison SET nom = ?, est_terminee = ? WHERE id = ?",
                    saison.getNom(),
                    saison.isEstTerminee(),
                    saison.getId()
            );
        }
        return saison;
    }

    public void terminerSaison(Integer saisonId) {
        jdbcTemplate.update(
                "UPDATE saison SET est_terminee = true WHERE id = ?",
                saisonId
        );
    }
}

