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
    public ResponseEntity<String> synchronizeData() {
        try {
            synchronizationService.synchronizeData();
            return new ResponseEntity<>("Synchronisation réussie", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la synchronisation: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/bestPlayers")
    public ResponseEntity<?> getBestPlayers(
            @RequestParam(defaultValue = "10") int top,
            @RequestParam(defaultValue = "total") String playingTimeUnit) {

        try {
            if (top <= 0) {
                return new ResponseEntity<>("Le paramètre 'top' doit être un nombre positif",
                        HttpStatus.BAD_REQUEST);
            }

            if (!playingTimeUnit.equals("total") && !playingTimeUnit.equals("efficiency")) {
                return new ResponseEntity<>("Le paramètre 'playingTimeUnit' doit être 'total' ou 'efficiency'",
                        HttpStatus.BAD_REQUEST);
            }

            List<Player> players = centralRepository.getBestPlayers(top, playingTimeUnit);
            return new ResponseEntity<>(players, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la récupération des meilleurs joueurs: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/championshipRankings")
    public ResponseEntity<List<Championship>> getChampionshipRankings() {
        List<Championship> rankings = centralRepository.getChampionshipRankings();
        return new ResponseEntity<>(rankings, HttpStatus.OK);
    }
}
