package org.example.centralapi.controller;

import org.example.centralapi.model.Championship;
import org.example.centralapi.model.Player;
import org.example.centralapi.repository.CentralRepository;
import org.example.centralapi.service.SynchronizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fifa")
public class CentralController {

    private final SynchronizationService synchronizationService;
    private final CentralRepository centralRepository;

    @Autowired
    public CentralController(SynchronizationService synchronizationService, CentralRepository centralRepository) {
        this.synchronizationService = synchronizationService;
        this.centralRepository = centralRepository;
    }

    @PostMapping("/synchronization")
    public ResponseEntity<String> synchronizeData(
            @RequestParam(required = false) String competition) {
        try {
            if (competition != null) {
                synchronizationService.synchronizeChampionship(competition);
                return new ResponseEntity<>("Synchronisation réussie pour: " + competition, HttpStatus.OK);
            } else {
                synchronizationService.synchronizeAllChampionships();
                return new ResponseEntity<>("Synchronisation complète réussie", HttpStatus.OK);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Championnat non configuré: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la synchronisation: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/bestPlayers")
    public ResponseEntity<List<Player>> getBestPlayers(
            @RequestParam(defaultValue = "10") int top,
            @RequestParam(defaultValue = "total") String playingTimeUnit) {

        List<Player> players = centralRepository.getBestPlayers(top, playingTimeUnit);
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @GetMapping("/championshipRankings")
    public ResponseEntity<List<Championship>> getChampionshipRankings() {
        List<Championship> rankings = centralRepository.getChampionshipRankings();
        return new ResponseEntity<>(rankings, HttpStatus.OK);
    }
}