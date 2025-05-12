package org.example.centralapi.service;

import org.example.centralapi.config.ChampionshipApiConfig;
import org.example.centralapi.model.Championship;
import org.example.centralapi.model.Club;
import org.example.centralapi.model.ClubStatistics;
import org.example.centralapi.model.Player;
import org.example.centralapi.repository.CentralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SynchronizationService {

    private final RestTemplate restTemplate;
    private final ChampionshipApiConfig apiConfig;
    private final CentralRepository centralRepository;

    @Autowired
    public SynchronizationService(
            RestTemplate restTemplate,
            ChampionshipApiConfig apiConfig,
            CentralRepository centralRepository) {
        this.restTemplate = restTemplate;
        this.apiConfig = apiConfig;
        this.centralRepository = centralRepository;
    }

    public void synchronizeData() {
        List<Championship> championships = new ArrayList<>();
        List<Club> allClubs = new ArrayList<>();
        List<Player> allPlayers = new ArrayList<>();
        List<ClubStatistics> allStatistics = new ArrayList<>();

        // Parcourir chaque API de championnat
        for (ChampionshipApiConfig.ApiConfig api : apiConfig.getApis()) {
            String baseUrl = api.getUrl();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", api.getKey());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            try {
                // 1. Récupérer les clubs
                ResponseEntity<List<Club>> clubsResponse = restTemplate.exchange(
                        baseUrl + "/clubs",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<List<Club>>() {}
                );
                List<Club> clubs = clubsResponse.getBody();
                if (clubs != null) {
                    allClubs.addAll(clubs);

                    // Pour chaque club, récupérer ses joueurs
                    for (Club club : clubs) {
                        ResponseEntity<List<Player>> playersResponse = restTemplate.exchange(
                                baseUrl + "/clubs/" + club.getId() + "/joueurs",
                                HttpMethod.GET,
                                entity,
                                new ParameterizedTypeReference<List<Player>>() {}
                        );
                        List<Player> players = playersResponse.getBody();
                        if (players != null) {
                            allPlayers.addAll(players);
                        }
                    }
                }

                // 2. Récupérer la saison courante
                ResponseEntity<Integer> currentSeasonResponse = restTemplate.exchange(
                        baseUrl + "/saisons/courante",
                        HttpMethod.GET,
                        entity,
                        Integer.class
                );
                Integer currentSeasonId = currentSeasonResponse.getBody();

                if (currentSeasonId != null) {
                    // 3. Récupérer les statistiques des clubs pour la saison courante
                    ResponseEntity<List<ClubStatistics>> statsResponse = restTemplate.exchange(
                            baseUrl + "/statistiques/collectives?saisonId=" + currentSeasonId,
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<List<ClubStatistics>>() {}
                    );
                    List<ClubStatistics> statistics = statsResponse.getBody();
                    if (statistics != null) {
                        allStatistics.addAll(statistics);
                    }
                }

                // Ajouter le championnat à la liste
                Championship championship = new Championship();
                championship.setId(api.getName().toLowerCase().replace(" ", "_"));
                championship.setName(api.getName());
                championship.setCountry(getCountryForChampionship(api.getName()));
                championships.add(championship);

            } catch (Exception e) {
                System.err.println("Erreur lors de la synchronisation avec " + api.getName() + ": " + e.getMessage());
            }
        }

        // Sauvegarder toutes les données dans la base de données centrale
        centralRepository.saveChampionships(championships);
        centralRepository.saveClubs(allClubs);
        centralRepository.savePlayers(allPlayers);
        centralRepository.saveStatistics(allStatistics);
    }

    private String getCountryForChampionship(String championshipName) {
        switch (championshipName) {
            case "Premier League": return "Angleterre";
            case "La Liga": return "Espagne";
            case "Bundesliga": return "Allemagne";
            case "Serie A": return "Italie";
            case "Ligue 1": return "France";
            default: return "Inconnu";
        }
    }
}