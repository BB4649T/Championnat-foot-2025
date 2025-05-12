package org.example.centralapi.service;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
        synchronizeAllChampionships();
    }

    public void synchronizeAllChampionships() {
        List<Championship> championships = new ArrayList<>();
        List<Club> allClubs = new ArrayList<>();
        List<Player> allPlayers = new ArrayList<>();
        List<ClubStatistics> allStatistics = new ArrayList<>();

        // Process each championship API
        for (ChampionshipApiConfig.ApiConfig api : apiConfig.getApis()) {
            try {
                synchronizeChampionship(api, championships, allClubs, allPlayers, allStatistics);
            } catch (Exception e) {
                log.error("Error synchronizing championship {}: {}", api.getName(), e.getMessage(), e);
            }
        }

        // Save all data to the central database
        centralRepository.saveChampionships(championships);
        centralRepository.saveClubs(allClubs);
        centralRepository.savePlayers(allPlayers);
        centralRepository.saveStatistics(allStatistics);
    }

    public void synchronizeChampionship(String championshipName) {
        Optional<ChampionshipApiConfig.ApiConfig> apiOpt = apiConfig.getApis().stream()
                .filter(api -> api.getName().equalsIgnoreCase(championshipName))
                .findFirst();

        if (apiOpt.isEmpty()) {
            throw new IllegalArgumentException("Championship not configured: " + championshipName);
        }

        List<Championship> championships = new ArrayList<>();
        List<Club> allClubs = new ArrayList<>();
        List<Player> allPlayers = new ArrayList<>();
        List<ClubStatistics> allStatistics = new ArrayList<>();

        synchronizeChampionship(apiOpt.get(), championships, allClubs, allPlayers, allStatistics);

        // Save the data for this championship only
        centralRepository.saveChampionships(championships);
        centralRepository.saveClubs(allClubs);
        centralRepository.savePlayers(allPlayers);
        centralRepository.saveStatistics(allStatistics);
    }

    private void synchronizeChampionship(
            ChampionshipApiConfig.ApiConfig api,
            List<Championship> championships,
            List<Club> allClubs,
            List<Player> allPlayers,
            List<ClubStatistics> allStatistics) {

        String baseUrl = api.getUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", api.getKey());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 1. Retrieve clubs
            ResponseEntity<List<Club>> clubsResponse = restTemplate.exchange(
                    baseUrl + "/clubs",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Club>>() {}
            );

            List<Club> clubs = clubsResponse.getBody();
            if (clubs != null) {
                // Set championship name for each club
                clubs.forEach(club -> club.setChampionshipName(api.getName()));
                allClubs.addAll(clubs);

                // For each club, retrieve its players
                for (Club club : clubs) {
                    try {
                        ResponseEntity<List<Player>> playersResponse = restTemplate.exchange(
                                baseUrl + "/clubs/" + club.getId() + "/joueurs",
                                HttpMethod.GET,
                                entity,
                                new ParameterizedTypeReference<List<Player>>() {}
                        );
                        List<Player> players = playersResponse.getBody();
                        if (players != null) {
                            // Set club name for each player
                            players.forEach(player -> player.setClubName(club.getName()));
                            allPlayers.addAll(players);
                        }
                    } catch (RestClientException e) {
                        log.warn("Error retrieving players for club {}: {}", club.getId(), e.getMessage());
                    }
                }
            }

            // 2. Retrieve current season ID
            ResponseEntity<Integer> currentSeasonResponse = restTemplate.exchange(
                    baseUrl + "/saisons/courante",
                    HttpMethod.GET,
                    entity,
                    Integer.class
            );

            Integer currentSeasonId = currentSeasonResponse.getBody();
            if (currentSeasonId != null) {
                // 3. Retrieve club statistics for current season
                ResponseEntity<List<ClubStatistics>> statsResponse = restTemplate.exchange(
                        baseUrl + "/statistiques/collectives?saisonId=" + currentSeasonId,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<List<ClubStatistics>>() {}
                );

                List<ClubStatistics> statistics = statsResponse.getBody();
                if (statistics != null) {
                    // Set championship name for each statistic
                    statistics.forEach(stat -> {
                        if (stat.getChampionshipName() == null) {
                            stat.setChampionshipName(api.getName());
                        }
                    });
                    allStatistics.addAll(statistics);
                }
            }

            // Add championship to the list
            Championship championship = new Championship();
            championship.setId(api.getName().toLowerCase().replace(" ", "_"));
            championship.setName(api.getName());
            championship.setCountry(getCountryForChampionship(api.getName()));
            championships.add(championship);

        } catch (Exception e) {
            throw new RuntimeException("Error synchronizing championship " + api.getName(), e);
        }
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