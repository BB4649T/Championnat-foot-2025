package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Championnat;
import com.example.demo.repository.ChampionnatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChampionnatService {

    private final ChampionnatRepository championnatRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ChampionnatService(ChampionnatRepository championnatRepository, JdbcTemplate jdbcTemplate) {
        this.championnatRepository = championnatRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // Méthodes existantes...
    public List<Championnat> getAllChampionnats() {
        return championnatRepository.findAll();
    }

    public Championnat getChampionnatById(Integer id) {
        return championnatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Championnat non trouvé avec l'id: " + id));
    }

    public Championnat createChampionnat(Championnat championnat) {
        return championnatRepository.save(championnat);
    }

    public Championnat updateChampionnat(Integer id, Championnat championnatDetails) {
        Championnat championnat = getChampionnatById(id);
        championnat.setNom(championnatDetails.getNom());
        championnat.setPays(championnatDetails.getPays());
        return championnatRepository.save(championnat);
    }

    public void deleteChampionnat(Integer id) {
        Championnat championnat = getChampionnatById(id);
        championnatRepository.deleteById(id);
    }

    // Nouvelle méthode pour obtenir les meilleurs championnats selon un critère
    public List<Championnat> getMeilleursChampionnats(String critere) {
        String sql;

        switch (critere.toLowerCase()) {
            case "buts":
                // Championnats avec le plus de buts marqués (meilleure attaque collective)
                sql = "SELECT c.id, c.nom, c.pays, SUM(sc.buts_marques) as total_buts " +
                        "FROM championnat c " +
                        "JOIN club cl ON c.id = cl.championnat_id " +
                        "JOIN statistiques_collectives sc ON cl.id = sc.club_id " +
                        "GROUP BY c.id, c.nom, c.pays " +
                        "ORDER BY total_buts DESC";
                break;

            case "cleansheets":
                // Championnats avec le plus de clean sheets (meilleure défense collective)
                sql = "SELECT c.id, c.nom, c.pays, SUM(sc.clean_sheets) as total_cleansheets " +
                        "FROM championnat c " +
                        "JOIN club cl ON c.id = cl.championnat_id " +
                        "JOIN statistiques_collectives sc ON cl.id = sc.club_id " +
                        "GROUP BY c.id, c.nom, c.pays " +
                        "ORDER BY total_cleansheets DESC";
                break;

            case "competitivite":
                // Championnats les plus compétitifs (écart minimum entre premier et dernier)
                sql = "WITH championship_gaps AS (" +
                        "  SELECT c.id, c.nom, c.pays, " +
                        "         MAX(sc.points) - MIN(sc.points) as point_gap " +
                        "  FROM championnat c " +
                        "  JOIN club cl ON c.id = cl.championnat_id " +
                        "  JOIN statistiques_collectives sc ON cl.id = sc.club_id " +
                        "  GROUP BY c.id, c.nom, c.pays" +
                        ") " +
                        "SELECT id, nom, pays FROM championship_gaps " +
                        "ORDER BY point_gap ASC"; // Plus petit écart = plus compétitif
                break;

            case "defense":
                // Championnats avec le moins de buts encaissés en moyenne
                sql = "SELECT c.id, c.nom, c.pays, AVG(sc.buts_encaisses) as avg_buts_encaisses " +
                        "FROM championnat c " +
                        "JOIN club cl ON c.id = cl.championnat_id " +
                        "JOIN statistiques_collectives sc ON cl.id = sc.club_id " +
                        "GROUP BY c.id, c.nom, c.pays " +
                        "ORDER BY avg_buts_encaisses ASC";
                break;

            case "champion":
                // Championnats avec le champion le plus performant (plus de points)
                sql = "WITH champions AS (" +
                        "  SELECT c.id as championnat_id, c.nom as championnat_nom, c.pays, " +
                        "         cl.id as club_id, cl.nom as club_nom, sc.points, " +
                        "         RANK() OVER (PARTITION BY c.id ORDER BY sc.points DESC, sc.difference_buts DESC) as rang " +
                        "  FROM championnat c " +
                        "  JOIN club cl ON c.id = cl.championnat_id " +
                        "  JOIN statistiques_collectives sc ON cl.id = sc.club_id " +
                        "  JOIN saison s ON sc.saison_id = s.id " +
                        "  WHERE s.est_terminee = false " +
                        ") " +
                        "SELECT championnat_id as id, championnat_nom as nom, pays, points " +
                        "FROM champions " +
                        "WHERE rang = 1 " +
                        "ORDER BY points DESC";
                break;

            default:
                // Par défaut, on classe par nombre total de buts
                sql = "SELECT c.id, c.nom, c.pays, SUM(sc.buts_marques) as total_buts " +
                        "FROM championnat c " +
                        "JOIN club cl ON c.id = cl.championnat_id " +
                        "JOIN statistiques_collectives sc ON cl.id = sc.club_id " +
                        "GROUP BY c.id, c.nom, c.pays " +
                        "ORDER BY total_buts DESC";
                break;
        }

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Championnat(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("pays")
                )
        );
    }
}