package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.StatistiquesCollectivesRepository;
import com.example.demo.repository.StatistiquesIndividuellesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StatistiquesService {

    private final StatistiquesCollectivesRepository statistiquesCollectivesRepository;
    private final StatistiquesIndividuellesRepository statistiquesIndividuellesRepository;
    private final ClubService clubService;
    private final JoueurService joueurService;
    private final SaisonService saisonService;

    @Autowired
    public StatistiquesService(StatistiquesCollectivesRepository statistiquesCollectivesRepository,
                               StatistiquesIndividuellesRepository statistiquesIndividuellesRepository,
                               ClubService clubService,
                               JoueurService joueurService,
                               SaisonService saisonService) {
        this.statistiquesCollectivesRepository = statistiquesCollectivesRepository;
        this.statistiquesIndividuellesRepository = statistiquesIndividuellesRepository;
        this.clubService = clubService;
        this.joueurService = joueurService;
        this.saisonService = saisonService;
    }

    public List<StatistiquesCollectives> getAllStatistiquesCollectives() {
        return statistiquesCollectivesRepository.findAll();
    }

    public StatistiquesCollectives getStatistiquesCollectivesByClubAndSaison(Integer clubId, Integer saisonId) {
        // Vérifier si le club et la saison existent
        clubService.getClubById(clubId);
        saisonService.getSaisonById(saisonId);

        return statistiquesCollectivesRepository.findByClubIdAndSaisonId(clubId, saisonId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Statistiques non trouvées pour le club id: " + clubId + " et la saison id: " + saisonId));
    }

    public List<StatistiquesIndividuelles> getAllStatistiquesIndividuelles() {
        return statistiquesIndividuellesRepository.findAll();
    }

    public StatistiquesIndividuelles getStatistiquesIndividuellesByJoueurAndSaison(Integer joueurId, Integer saisonId) {
        // Vérifier si le joueur et la saison existent
        joueurService.getJoueurById(joueurId);
        saisonService.getSaisonById(saisonId);

        return statistiquesIndividuellesRepository.findByJoueurIdAndSaisonId(joueurId, saisonId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Statistiques non trouvées pour le joueur id: " + joueurId + " et la saison id: " + saisonId));
    }

    @Transactional
    public void mettreAJourStatistiquesApresMatch(Match match) {
        if (!match.isEstJoue()) {
            throw new IllegalArgumentException("Le match n'a pas encore été joué");
        }

        Integer saisonId = match.getSaisonId();
        Integer clubDomicileId = match.getClubDomicileId();
        Integer clubExterieurId = match.getClubExterieurId();
        Integer scoreDomicile = match.getScoreDomicile();
        Integer scoreExterieur = match.getScoreExterieur();

        // Récupérer ou initialiser les statistiques des clubs
        StatistiquesCollectives statsDomicile = getOrCreateStatistiquesCollectives(clubDomicileId, saisonId);
        StatistiquesCollectives statsExterieur = getOrCreateStatistiquesCollectives(clubExterieurId, saisonId);

        // Mettre à jour les buts marqués/encaissés
        statsDomicile.setButsMarques(statsDomicile.getButsMarques() + scoreDomicile);
        statsDomicile.setButsEncaisses(statsDomicile.getButsEncaisses() + scoreExterieur);
        statsExterieur.setButsMarques(statsExterieur.getButsMarques() + scoreExterieur);
        statsExterieur.setButsEncaisses(statsExterieur.getButsEncaisses() + scoreDomicile);

        // Mettre à jour la différence de buts
        statsDomicile.setDifferenceButs(statsDomicile.getButsMarques() - statsDomicile.getButsEncaisses());
        statsExterieur.setDifferenceButs(statsExterieur.getButsMarques() - statsExterieur.getButsEncaisses());

        // Mettre à jour les clean sheets
        if (scoreExterieur == 0) {
            statsDomicile.setCleanSheets(statsDomicile.getCleanSheets() + 1);
        }
        if (scoreDomicile == 0) {
            statsExterieur.setCleanSheets(statsExterieur.getCleanSheets() + 1);
        }

        // Attribuer les points (3 pour victoire, 1 pour nul, 0 pour défaite)
        if (scoreDomicile > scoreExterieur) {
            // Victoire domicile
            statsDomicile.setPoints(statsDomicile.getPoints() + 3);
        } else if (scoreDomicile < scoreExterieur) {
            // Victoire extérieur
            statsExterieur.setPoints(statsExterieur.getPoints() + 3);
        } else {
            // Match nul
            statsDomicile.setPoints(statsDomicile.getPoints() + 1);
            statsExterieur.setPoints(statsExterieur.getPoints() + 1);
        }

        // Enregistrer les mises à jour
        statistiquesCollectivesRepository.save(statsDomicile);
        statistiquesCollectivesRepository.save(statsExterieur);
    }

    public List<Classement> getClassementByChampionnatAndSaison(Integer championnatId, Integer saisonId) {
        // Vérifier si la saison existe
        saisonService.getSaisonById(saisonId);

        return statistiquesCollectivesRepository.getClassementByChampionnatAndSaison(championnatId, saisonId);
    }

    private StatistiquesCollectives getOrCreateStatistiquesCollectives(Integer clubId, Integer saisonId) {
        Optional<StatistiquesCollectives> optStats =
                statistiquesCollectivesRepository.findByClubIdAndSaisonId(clubId, saisonId);

        if (optStats.isPresent()) {
            return optStats.get();
        } else {
            // Créer de nouvelles statistiques pour ce club et cette saison
            StatistiquesCollectives newStats = new StatistiquesCollectives();
            newStats.setClubId(clubId);
            newStats.setSaisonId(saisonId);
            newStats.setPoints(0);
            newStats.setButsMarques(0);
            newStats.setButsEncaisses(0);
            newStats.setDifferenceButs(0);
            newStats.setCleanSheets(0);
            return statistiquesCollectivesRepository.save(newStats);
        }
    }

    @Transactional
    public void updatePlayerStatistics(Integer joueurId, Integer saisonId, Integer buts, Integer minutes) {
        // Vérifier si le joueur et la saison existent
        joueurService.getJoueurById(joueurId);
        saisonService.getSaisonById(saisonId);

        // Récupérer ou créer les statistiques du joueur
        Optional<StatistiquesIndividuelles> optStats =
                statistiquesIndividuellesRepository.findByJoueurIdAndSaisonId(joueurId, saisonId);

        StatistiquesIndividuelles stats;
        if (optStats.isPresent()) {
            stats = optStats.get();
            stats.setButsMarques(stats.getButsMarques() + buts);
            stats.setDureeJeu(stats.getDureeJeu() + minutes);
        } else {
            stats = new StatistiquesIndividuelles();
            stats.setJoueurId(joueurId);
            stats.setSaisonId(saisonId);
            stats.setButsMarques(buts);
            stats.setDureeJeu(minutes);
        }

        statistiquesIndividuellesRepository.save(stats);
    }
    public void mettreAJourStatistiquesCollectivesButMarque(Integer clubId, Integer saisonId) {
        StatistiquesCollectives stats = getOrCreateStatistiquesCollectives(clubId, saisonId);
        stats.setButsMarques(stats.getButsMarques() + 1);
        stats.setDifferenceButs(stats.getButsMarques() - stats.getButsEncaisses());
        statistiquesCollectivesRepository.save(stats);
    }

    public void mettreAJourStatistiquesCollectivesButEncaisse(Integer clubId, Integer saisonId) {
        StatistiquesCollectives stats = getOrCreateStatistiquesCollectives(clubId, saisonId);
        stats.setButsEncaisses(stats.getButsEncaisses() + 1);
        stats.setDifferenceButs(stats.getButsMarques() - stats.getButsEncaisses());
        statistiquesCollectivesRepository.save(stats);
    }
}
