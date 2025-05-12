package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final ClubService clubService;
    private final SaisonService saisonService;
    private final StatistiquesService statistiquesService;
    private final JoueurService joueurService; // Ajout de cette dépendance

    @Autowired
    public MatchService(MatchRepository matchRepository,
                        ClubService clubService,
                        SaisonService saisonService,
                        StatistiquesService statistiquesService,
                        JoueurService joueurService) { // Ajout au constructeur
        this.matchRepository = matchRepository;
        this.clubService = clubService;
        this.saisonService = saisonService;
        this.statistiquesService = statistiquesService;
        this.joueurService = joueurService;
    }

    public List<Match> getAllMatchs() {
        return matchRepository.findAll();
    }

    public List<Match> getMatchsBySaison(Integer saisonId) {
        // Vérifier si la saison existe
        saisonService.getSaisonById(saisonId);
        return matchRepository.findBySaisonId(saisonId);
    }

    public List<Match> getMatchsByClubAndSaison(Integer clubId, Integer saisonId) {
        // Vérifier si le club et la saison existent
        clubService.getClubById(clubId);
        saisonService.getSaisonById(saisonId);

        return matchRepository.findByClubIdAndSaisonId(clubId, saisonId);
    }

    public Match getMatchById(Integer id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match non trouvé avec l'id: " + id));
    }

    public Match createMatch(Match match) {
        // Vérifier si les clubs existent
        clubService.getClubById(match.getClubDomicileId());
        clubService.getClubById(match.getClubExterieurId());

        // Vérifier si la saison existe
        saisonService.getSaisonById(match.getSaisonId());

        // Vérifier que le club domicile et extérieur sont différents
        if (match.getClubDomicileId().equals(match.getClubExterieurId())) {
            throw new IllegalArgumentException("Le club domicile et le club extérieur ne peuvent pas être identiques");
        }

        // Initialiser les valeurs par défaut
        match.setEstJoue(false);
        if (match.getScoreDomicile() == null) match.setScoreDomicile(0);
        if (match.getScoreExterieur() == null) match.setScoreExterieur(0);

        return matchRepository.save(match);
    }

    public Match updateMatch(Integer id, Match matchDetails) {
        Match match = getMatchById(id);

        // Si le match est déjà joué, on ne peut pas le modifier
        if (match.isEstJoue()) {
            throw new IllegalStateException("Impossible de modifier un match déjà joué");
        }

        if (matchDetails.getClubDomicileId() != null) {
            clubService.getClubById(matchDetails.getClubDomicileId());
            match.setClubDomicileId(matchDetails.getClubDomicileId());
        }

        if (matchDetails.getClubExterieurId() != null) {
            clubService.getClubById(matchDetails.getClubExterieurId());
            match.setClubExterieurId(matchDetails.getClubExterieurId());
        }

        // Vérifier que le club domicile et extérieur sont différents
        if (match.getClubDomicileId().equals(match.getClubExterieurId())) {
            throw new IllegalArgumentException("Le club domicile et le club extérieur ne peuvent pas être identiques");
        }

        if (matchDetails.getStade() != null) {
            match.setStade(matchDetails.getStade());
        }

        if (matchDetails.getDateHeure() != null) {
            match.setDateHeure(matchDetails.getDateHeure());
        }

        if (matchDetails.getSaisonId() != null) {
            saisonService.getSaisonById(matchDetails.getSaisonId());
            match.setSaisonId(matchDetails.getSaisonId());
        }

        return matchRepository.save(match);
    }

    @Transactional
    public Match enregistrerResultat(ResultatMatch resultatMatch) {
        Match match = getMatchById(resultatMatch.getMatchId());

        // Vérifier si le match n'est pas déjà joué
        if (match.isEstJoue()) {
            throw new IllegalStateException("Le résultat de ce match a déjà été enregistré");
        }

        // Vérifier si la saison est toujours en cours
        Saison saison = saisonService.getSaisonById(match.getSaisonId());
        if (saison.isEstTerminee()) {
            throw new IllegalStateException("Impossible d'enregistrer un résultat pour une saison terminée");
        }

        // Mettre à jour le score et le statut du match
        match.setScoreDomicile(resultatMatch.getScoreDomicile());
        match.setScoreExterieur(resultatMatch.getScoreExterieur());
        match.setEstJoue(true);

        // Enregistrer les modifications du match
        Match updatedMatch = matchRepository.save(match);

        // Mettre à jour les statistiques des équipes
        statistiquesService.mettreAJourStatistiquesApresMatch(match);

        return updatedMatch;
    }

    public void deleteMatch(Integer id) {
        Match match = getMatchById(id);

        // Si le match est déjà joué, on ne peut pas le supprimer
        if (match.isEstJoue()) {
            throw new IllegalStateException("Impossible de supprimer un match déjà joué");
        }

        matchRepository.deleteById(id);
    }

    @Transactional
    public List<Match> genererCalendrierSaison(Integer saisonId) {
        // Vérifier si la saison existe
        Saison saison = saisonService.getSaisonById(saisonId);

        // Vérifier si la saison n'est pas déjà terminée
        if (saison.isEstTerminee()) {
            throw new IllegalStateException("Impossible de générer le calendrier pour une saison terminée");
        }

        // Vérifier s'il existe déjà des matchs pour cette saison
        List<Match> matchsExistants = matchRepository.findBySaisonId(saisonId);
        if (!matchsExistants.isEmpty()) {
            throw new IllegalStateException("Des matchs existent déjà pour cette saison");
        }

        // Récupérer tous les clubs
        List<Club> clubs = clubService.getAllClubs();

        if (clubs.size() < 2) {
            throw new IllegalStateException("Il faut au moins 2 clubs pour générer un calendrier");
        }

        List<Match> matchsGeneres = new ArrayList<>();

        // Générer les matchs aller
        for (int i = 0; i < clubs.size(); i++) {
            for (int j = i + 1; j < clubs.size(); j++) {
                Club clubDomicile = clubs.get(i);
                Club clubExterieur = clubs.get(j);

                // Vérifier qu'ils sont dans le même championnat
                if (!clubDomicile.getChampionnatId().equals(clubExterieur.getChampionnatId())) {
                    continue;
                }

                Match matchAller = new Match();
                matchAller.setClubDomicileId(clubDomicile.getId());
                matchAller.setClubExterieurId(clubExterieur.getId());
                matchAller.setStade(clubDomicile.getNomStade());
                matchAller.setSaisonId(saisonId);
                matchAller.setEstJoue(false);
                matchAller.setScoreDomicile(0);
                matchAller.setScoreExterieur(0);

                // Date initialement null
                matchAller.setDateHeure(null);

                matchsGeneres.add(matchRepository.save(matchAller));

                // Match retour
                Match matchRetour = new Match();
                matchRetour.setClubDomicileId(clubExterieur.getId());
                matchRetour.setClubExterieurId(clubDomicile.getId());
                matchRetour.setStade(clubExterieur.getNomStade());
                matchRetour.setSaisonId(saisonId);
                matchRetour.setEstJoue(false);
                matchRetour.setScoreDomicile(0);
                matchRetour.setScoreExterieur(0);

                // Date initialement null
                matchRetour.setDateHeure(null);

                matchsGeneres.add(matchRepository.save(matchRetour));
            }
        }

        return matchsGeneres;
    }
    public Match updateMatchStatus(Integer id, Boolean estJoue) {
        Match match = getMatchById(id);
        match.setEstJoue(estJoue);
        return matchRepository.save(match);
    }
    @Transactional
    public Match addGoal(Integer matchId, Integer joueurId, Integer saisonId, String equipe, Integer minute) {
        Match match = getMatchById(matchId);

        if (!match.isEstJoue()) {
            throw new IllegalStateException("Impossible d'ajouter un but à un match non joué");
        }

        // Vérifier si le joueur existe
        Joueur joueur = joueurService.getJoueurById(joueurId);

        // Mettre à jour le score du match
        if ("domicile".equalsIgnoreCase(equipe)) {
            match.setScoreDomicile(match.getScoreDomicile() + 1);
        } else if ("exterieur".equalsIgnoreCase(equipe)) {
            match.setScoreExterieur(match.getScoreExterieur() + 1);
        } else {
            throw new IllegalArgumentException("L'équipe doit être 'domicile' ou 'exterieur'");
        }

        // Enregistrer le but dans les statistiques du joueur
        statistiquesService.updatePlayerStatistics(joueurId, saisonId, 1, minute);

        // Mettre à jour les statistiques collectives
        statistiquesService.mettreAJourStatistiquesApresMatch(match);

        return matchRepository.save(match);
    }

}
