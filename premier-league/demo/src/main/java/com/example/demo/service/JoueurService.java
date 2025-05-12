package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Joueur;
import com.example.demo.repository.JoueurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JoueurService {

    private final JoueurRepository joueurRepository;
    private final ClubService clubService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JoueurService(JoueurRepository joueurRepository, ClubService clubService, JdbcTemplate jdbcTemplate) {
        this.joueurRepository = joueurRepository;
        this.clubService = clubService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Joueur> getAllJoueurs() {
        return joueurRepository.findAll();
    }

    public List<Joueur> getJoueursByClubId(Integer clubId) {
        // Vérifier si le club existe
        clubService.getClubById(clubId);
        return joueurRepository.findByClubId(clubId);
    }

    public Joueur getJoueurById(Integer id) {
        return joueurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Joueur non trouvé avec l'id: " + id));
    }
    public List<Joueur> getFilteredJoueurs(Integer clubId, String nom, Integer ageMin, Integer ageMax) {
        List<Joueur> joueurs = joueurRepository.findAll();

        // Apply filters
        if (clubId != null) {
            joueurs = joueurs.stream()
                    .filter(j -> j.getClubId().equals(clubId))
                    .collect(Collectors.toList());
        }

        if (nom != null && !nom.isEmpty()) {
            joueurs = joueurs.stream()
                    .filter(j -> j.getNom().toLowerCase().contains(nom.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (ageMin != null) {
            joueurs = joueurs.stream()
                    .filter(j -> j.getAge() >= ageMin)
                    .collect(Collectors.toList());
        }

        if (ageMax != null) {
            joueurs = joueurs.stream()
                    .filter(j -> j.getAge() <= ageMax)
                    .collect(Collectors.toList());
        }

        return joueurs;
    }
    public Joueur createJoueur(Joueur joueur) {
        // Vérifier si le club existe
        clubService.getClubById(joueur.getClubId());

        try {
            return joueurRepository.save(joueur);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Le numéro " + joueur.getNumero() +
                    " est déjà utilisé dans ce club");
        }
    }

    public Joueur updateJoueur(Integer id, Joueur joueurDetails) {
        Joueur joueur = getJoueurById(id);
        joueur.setNom(joueurDetails.getNom());
        joueur.setNumero(joueurDetails.getNumero());
        joueur.setPoste(joueurDetails.getPoste());
        joueur.setNationalite(joueurDetails.getNationalite());
        joueur.setAge(joueurDetails.getAge());

        if (joueurDetails.getClubId() != null) {
            // Vérifier si le club existe
            clubService.getClubById(joueurDetails.getClubId());
            joueur.setClubId(joueurDetails.getClubId());
        }

        try {
            return joueurRepository.save(joueur);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Le numéro " + joueur.getNumero() +
                    " est déjà utilisé dans ce club");
        }
    }

    public void deleteJoueur(Integer id) {
        Joueur joueur = getJoueurById(id);
        joueurRepository.deleteById(id);
    }
    public List<Joueur> getJoueursByPoste(String poste) {
        return joueurRepository.findByPoste(poste);
    }
    // Ajout à JoueurService.java
    public List<Joueur> getMeilleursJoueurs(String critere) {
        String sql;

        switch (critere.toLowerCase()) {
            case "buts":
                // Joueurs avec le plus de buts
                sql = "SELECT j.*, c.nom as club_nom, si.buts_marques " +
                        "FROM joueur j " +
                        "JOIN club c ON j.club_id = c.id " +
                        "JOIN statistiques_individuelles si ON j.id = si.joueur_id " +
                        "JOIN saison s ON si.saison_id = s.id " +
                        "WHERE s.est_terminee = false " +
                        "ORDER BY si.buts_marques DESC";
                break;

            case "temps":
                // Joueurs avec le plus de temps de jeu
                sql = "SELECT j.*, c.nom as club_nom, si.duree_jeu " +
                        "FROM joueur j " +
                        "JOIN club c ON j.club_id = c.id " +
                        "JOIN statistiques_individuelles si ON j.id = si.joueur_id " +
                        "JOIN saison s ON si.saison_id = s.id " +
                        "WHERE s.est_terminee = false " +
                        "ORDER BY si.duree_jeu DESC";
                break;

            case "efficacite":
                // Joueurs avec la meilleure efficacité (buts par minute)
                sql = "SELECT j.*, c.nom as club_nom, " +
                        "CASE WHEN si.duree_jeu > 0 THEN (si.buts_marques * 90.0 / si.duree_jeu) ELSE 0 END as efficacite " +
                        "FROM joueur j " +
                        "JOIN club c ON j.club_id = c.id " +
                        "JOIN statistiques_individuelles si ON j.id = si.joueur_id " +
                        "JOIN saison s ON si.saison_id = s.id " +
                        "WHERE s.est_terminee = false AND si.duree_jeu > 0 " +
                        "ORDER BY efficacite DESC";
                break;

            case "jeunes":
                // Jeunes joueurs (moins de 23 ans) les plus performants
                sql = "SELECT j.*, c.nom as club_nom, si.buts_marques " +
                        "FROM joueur j " +
                        "JOIN club c ON j.club_id = c.id " +
                        "JOIN statistiques_individuelles si ON j.id = si.joueur_id " +
                        "JOIN saison s ON si.saison_id = s.id " +
                        "WHERE s.est_terminee = false AND j.age < 23 " +
                        "ORDER BY si.buts_marques DESC";
                break;

            default:
                // Par défaut, on classe par buts
                sql = "SELECT j.*, c.nom as club_nom, si.buts_marques " +
                        "FROM joueur j " +
                        "JOIN club c ON j.club_id = c.id " +
                        "JOIN statistiques_individuelles si ON j.id = si.joueur_id " +
                        "JOIN saison s ON si.saison_id = s.id " +
                        "WHERE s.est_terminee = false " +
                        "ORDER BY si.buts_marques DESC";
                break;
        }

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Joueur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("numero"),
                        rs.getString("poste"),
                        rs.getString("nationalite"),
                        rs.getInt("age"),
                        rs.getInt("club_id"),
                        rs.getString("club_nom")
                )
        );
    }
    public List<Joueur> getJoueursByFilter(String nom, Integer clubId, Integer ageMin, Integer ageMax) {
        return joueurRepository.findByFilter(nom, clubId, ageMin, ageMax);
    }

}

