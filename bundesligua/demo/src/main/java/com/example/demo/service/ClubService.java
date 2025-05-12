package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Club;
import com.example.demo.model.Joueur;
import com.example.demo.repository.ClubRepository;
import com.example.demo.repository.JoueurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubService {

    private final ClubRepository clubRepository;
    private final ChampionnatService championnatService;
    private final JdbcTemplate jdbcTemplate;
    private final JoueurRepository joueurRepository; // Ajout de cette dépendance

    @Autowired
    public ClubService(ClubRepository clubRepository,
                       ChampionnatService championnatService,
                       JdbcTemplate jdbcTemplate,
                       JoueurRepository joueurRepository) { // Ajout au constructeur
        this.clubRepository = clubRepository;
        this.championnatService = championnatService;
        this.jdbcTemplate = jdbcTemplate;
        this.joueurRepository = joueurRepository;
    }

    private boolean clubNameExists(String nom, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM club WHERE LOWER(nom) = LOWER(?)";
        Object[] params = {nom};

        if (excludeId != null) {
            sql += " AND id != ?";
            params = new Object[]{nom, excludeId};
        }

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, params);
        return count != null && count > 0;
    }

    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    public List<Club> getClubsByChampionnatId(Integer championnatId) {
        // Vérifier si le championnat existe
        championnatService.getChampionnatById(championnatId);
        return clubRepository.findByChampionnatId(championnatId);
    }

    public Club getClubById(Integer id) {
        return clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club non trouvé avec l'id: " + id));
    }

    public Club createClub(Club club) {
        // Vérifier si le championnat existe
        championnatService.getChampionnatById(club.getChampionnatId());

        // Vérifier si le nom du club existe déjà
        if (clubNameExists(club.getNom(), null)) {
            throw new IllegalArgumentException("Un club avec le nom '" + club.getNom() + "' existe déjà");
        }

        return clubRepository.save(club);
    }

    public Club updateClub(Integer id, Club clubDetails) {
        Club club = getClubById(id);

        // Vérifier si le nouveau nom existe déjà (sauf pour le club actuel)
        if (!club.getNom().equalsIgnoreCase(clubDetails.getNom()) &&
                clubNameExists(clubDetails.getNom(), id)) {
            throw new IllegalArgumentException("Un club avec le nom '" + clubDetails.getNom() + "' existe déjà");
        }

        club.setNom(clubDetails.getNom());
        club.setAcronyme(clubDetails.getAcronyme());
        club.setAnneeCreation(clubDetails.getAnneeCreation());
        club.setNomStade(clubDetails.getNomStade());

        if (clubDetails.getChampionnatId() != null) {
            // Vérifier si le championnat existe
            championnatService.getChampionnatById(clubDetails.getChampionnatId());
            club.setChampionnatId(clubDetails.getChampionnatId());
        }

        return clubRepository.save(club);
    }

    public void deleteClub(Integer id) {
        Club club = getClubById(id);
        clubRepository.deleteById(id);
    }
    // Ajout à ClubService.java
    public List<Club> getMeilleursClubs(String critere) {
        String sql;

        switch (critere.toLowerCase()) {
            case "points":
                // Clubs avec le plus de points
                sql = "SELECT c.*, ch.nom as championnat_nom, sc.points " +
                        "FROM club c " +
                        "JOIN championnat ch ON c.championnat_id = ch.id " +
                        "JOIN statistiques_collectives sc ON c.id = sc.club_id " +
                        "JOIN saison s ON sc.saison_id = s.id " +
                        "WHERE s.est_terminee = false " +
                        "ORDER BY sc.points DESC, sc.difference_buts DESC";
                break;

            case "buts":
                // Clubs avec la meilleure attaque
                sql = "SELECT c.*, ch.nom as championnat_nom, sc.buts_marques " +
                        "FROM club c " +
                        "JOIN championnat ch ON c.championnat_id = ch.id " +
                        "JOIN statistiques_collectives sc ON c.id = sc.club_id " +
                        "JOIN saison s ON sc.saison_id = s.id " +
                        "WHERE s.est_terminee = false " +
                        "ORDER BY sc.buts_marques DESC";
                break;

            case "defense":
                // Clubs avec la meilleure défense
                sql = "SELECT c.*, ch.nom as championnat_nom, sc.buts_encaisses, sc.clean_sheets " +
                        "FROM club c " +
                        "JOIN championnat ch ON c.championnat_id = ch.id " +
                        "JOIN statistiques_collectives sc ON c.id = sc.club_id " +
                        "JOIN saison s ON sc.saison_id = s.id " +
                        "WHERE s.est_terminee = false " +
                        "ORDER BY sc.buts_encaisses ASC, sc.clean_sheets DESC";
                break;

            case "difference":
                // Clubs avec la meilleure différence de buts
                sql = "SELECT c.*, ch.nom as championnat_nom, sc.difference_buts " +
                        "FROM club c " +
                        "JOIN championnat ch ON c.championnat_id = ch.id " +
                        "JOIN statistiques_collectives sc ON c.id = sc.club_id " +
                        "JOIN saison s ON sc.saison_id = s.id " +
                        "WHERE s.est_terminee = false " +
                        "ORDER BY sc.difference_buts DESC";
                break;

            case "cleansheets":
                // Clubs avec le plus de clean sheets
                sql = "SELECT c.*, ch.nom as championnat_nom, sc.clean_sheets " +
                        "FROM club c " +
                        "JOIN championnat ch ON c.championnat_id = ch.id " +
                        "JOIN statistiques_collectives sc ON c.id = sc.club_id " +
                        "JOIN saison s ON sc.saison_id = s.id " +
                        "WHERE s.est_terminee = false " +
                        "ORDER BY sc.clean_sheets DESC";
                break;

            default:
                // Par défaut, on classe par points
                sql = "SELECT c.*, ch.nom as championnat_nom, sc.points " +
                        "FROM club c " +
                        "JOIN championnat ch ON c.championnat_id = ch.id " +
                        "JOIN statistiques_collectives sc ON c.id = sc.club_id " +
                        "JOIN saison s ON sc.saison_id = s.id " +
                        "WHERE s.est_terminee = false " +
                        "ORDER BY sc.points DESC, sc.difference_buts DESC";
                break;
        }

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Club(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("acronyme"),
                        rs.getInt("annee_creation"),
                        rs.getString("nom_stade"),
                        rs.getInt("championnat_id"),
                        rs.getString("championnat_nom")
                )
        );
    }
    @Transactional
    public List<Joueur> updateJoueursFromClub(Integer clubId, List<Integer> joueursIds) {
        // Vérifier si le club existe
        Club club = getClubById(clubId);

        // Obtenir tous les joueurs actuels du club
        List<Joueur> joueursActuels = joueurRepository.findByClubId(clubId);

        // Supprimer les joueurs qui ne sont plus dans le club
        List<Integer> joueursIdsActuels = joueursActuels.stream()
                .map(Joueur::getId)
                .collect(Collectors.toList());

        // Joueurs à retirer du club (ceux qui étaient dans le club mais ne sont plus dans la nouvelle liste)
        joueursIdsActuels.stream()
                .filter(id -> !joueursIds.contains(id))
                .forEach(id -> {
                    Joueur joueur = joueurRepository.findById(id).orElse(null);
                    if (joueur != null) {
                        // Mettre à jour le joueur pour qu'il n'ait plus de club (null ou autre valeur par défaut)
                        joueur.setClubId(null);
                        joueurRepository.save(joueur);
                    }
                });

        // Ajouter les nouveaux joueurs au club
        List<Joueur> joueursAJour = new ArrayList<>();
        for (Integer joueurId : joueursIds) {
            Joueur joueur = joueurRepository.findById(joueurId)
                    .orElseThrow(() -> new ResourceNotFoundException("Joueur non trouvé avec l'id: " + joueurId));
            joueur.setClubId(clubId);
            joueursAJour.add(joueurRepository.save(joueur));
        }

        return joueursAJour;
    }

}

