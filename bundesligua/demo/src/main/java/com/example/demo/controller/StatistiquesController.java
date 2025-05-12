package com.example.demo.controller;

import com.example.demo.model.StatistiquesCollectives;
import com.example.demo.model.StatistiquesIndividuelles;
import com.example.demo.service.StatistiquesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistiques")
public class StatistiquesController {

    private final StatistiquesService statistiquesService;

    @Autowired
    public StatistiquesController(StatistiquesService statistiquesService) {
        this.statistiquesService = statistiquesService;
    }

    @GetMapping("/collectives")
    public ResponseEntity<List<StatistiquesCollectives>> getAllStatistiquesCollectives() {
        List<StatistiquesCollectives> stats = statistiquesService.getAllStatistiquesCollectives();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping("/collectives/club/{clubId}/saison/{saisonId}")
    public ResponseEntity<StatistiquesCollectives> getStatistiquesCollectivesByClubAndSaison(
            @PathVariable Integer clubId,
            @PathVariable Integer saisonId) {
        StatistiquesCollectives stats = statistiquesService.getStatistiquesCollectivesByClubAndSaison(clubId, saisonId);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping("/individuelles")
    public ResponseEntity<List<StatistiquesIndividuelles>> getAllStatistiquesIndividuelles() {
        List<StatistiquesIndividuelles> stats = statistiquesService.getAllStatistiquesIndividuelles();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping("/individuelles/joueur/{joueurId}/saison/{saisonId}")
    public ResponseEntity<StatistiquesIndividuelles> getStatistiquesIndividuellesByJoueurAndSaison(
            @PathVariable Integer joueurId,
            @PathVariable Integer saisonId) {
        StatistiquesIndividuelles stats = statistiquesService.getStatistiquesIndividuellesByJoueurAndSaison(joueurId, saisonId);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @PostMapping("/individuelles/update")
    public ResponseEntity<Void> updatePlayerStatistics(@RequestBody Map<String, Object> statsData) {
        Integer joueurId = (Integer) statsData.get("joueurId");
        Integer saisonId = (Integer) statsData.get("saisonId");
        Integer buts = (Integer) statsData.get("buts");
        Integer minutes = (Integer) statsData.get("minutes");

        statistiquesService.updatePlayerStatistics(joueurId, saisonId, buts, minutes);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
