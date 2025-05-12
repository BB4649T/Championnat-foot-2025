package com.example.demo.repository;

import com.example.demo.model.Championnat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChampionnatRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ChampionnatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Championnat> rowMapper = (rs, rowNum) ->
            new Championnat(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("pays")
            );

    public List<Championnat> findAll() {
        return jdbcTemplate.query("SELECT * FROM championnat", rowMapper);
    }

    public Optional<Championnat> findById(Integer id) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM championnat WHERE id = ?",
                            rowMapper,
                            id
                    )
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Championnat save(Championnat championnat) {
        if (championnat.getId() == null) {
            Integer id = jdbcTemplate.queryForObject(
                    "INSERT INTO championnat (nom, pays) VALUES (?, ?) RETURNING id",
                    Integer.class,
                    championnat.getNom(),
                    championnat.getPays()
            );
            championnat.setId(id);
        } else {
            jdbcTemplate.update(
                    "UPDATE championnat SET nom = ?, pays = ? WHERE id = ?",
                    championnat.getNom(),
                    championnat.getPays(),
                    championnat.getId()
            );
        }
        return championnat;
    }

    public void deleteById(Integer id) {
        jdbcTemplate.update("DELETE FROM championnat WHERE id = ?", id);
    }
}

