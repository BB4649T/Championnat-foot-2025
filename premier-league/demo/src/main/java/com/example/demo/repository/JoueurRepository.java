package com.example.demo.repository;

import com.example.demo.model.Joueur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JoueurRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JoueurRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Joueur> rowMapper = (rs, rowNum) ->
            new Joueur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getInt("numero"),
                    rs.getString("poste"),
                    rs.getString("nationalite"),
                    rs.getInt("age"),
                    rs.getInt("club_id"),
                    rs.getString("club_nom")
            );

    public List<Joueur> findAll() {
        return jdbcTemplate.query(
                "SELECT j.*, c.nom as club_nom FROM joueur j " +
                        "JOIN club c ON j.club_id = c.id",
                rowMapper
        );
    }

    public List<Joueur> findByClubId(Integer clubId) {
        return jdbcTemplate.query(
                "SELECT j.*, c.nom as club_nom FROM joueur j " +
                        "JOIN club c ON j.club_id = c.id " +
                        "WHERE j.club_id = ?",
                rowMapper,
                clubId
        );
    }

    public Optional<Joueur> findById(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT j.*, c.nom as club_nom FROM joueur j " +
                                    "JOIN club c ON j.club_id = c.id " +
                                    "WHERE j.id = ?",
                            rowMapper,
                            id
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Joueur save(Joueur joueur) {
        if (joueur.getId() == null) {
            Integer id = jdbcTemplate.queryForObject(
                    "INSERT INTO joueur (nom, numero, poste, nationalite, age, club_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?) RETURNING id",
                    Integer.class,
                    joueur.getNom(),
                    joueur.getNumero(),
                    joueur.getPoste(),
                    joueur.getNationalite(),
                    joueur.getAge(),
                    joueur.getClubId()
            );
            joueur.setId(id);
        } else {
            jdbcTemplate.update(
                    "UPDATE joueur SET nom = ?, numero = ?, poste = ?, nationalite = ?, age = ?, club_id = ? " +
                            "WHERE id = ?",
                    joueur.getNom(),
                    joueur.getNumero(),
                    joueur.getPoste(),
                    joueur.getNationalite(),
                    joueur.getAge(),
                    joueur.getClubId(),
                    joueur.getId()
            );
        }
        return joueur;
    }

    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM joueur WHERE id = ?", id);
    }
    public List<Joueur> findByPoste(String poste) {
        return jdbcTemplate.query(
                "SELECT j.*, c.nom as club_nom FROM joueur j " +
                        "JOIN club c ON j.club_id = c.id " +
                        "WHERE j.poste = ?",
                rowMapper,
                poste
        );
    }
    public List<Joueur> findByFilter(String nom, Integer clubId, Integer ageMin, Integer ageMax) {
        StringBuilder sql = new StringBuilder(
                "SELECT j.*, c.nom as club_nom FROM joueur j " +
                        "JOIN club c ON j.club_id = c.id WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (nom != null && !nom.trim().isEmpty()) {
            sql.append(" AND LOWER(j.nom) LIKE LOWER(?)");
            params.add("%" + nom + "%");
        }

        if (clubId != null) {
            sql.append(" AND j.club_id = ?");
            params.add(clubId);
        }

        if (ageMin != null) {
            sql.append(" AND j.age >= ?");
            params.add(ageMin);
        }

        if (ageMax != null) {
            sql.append(" AND j.age <= ?");
            params.add(ageMax);
        }

        return jdbcTemplate.query(sql.toString(), rowMapper, params.toArray());
    }

}

