package com.example.demo.controller;

import com.example.demo.model.Match;
import com.example.demo.model.ResultatMatch;
import com.example.demo.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matchs")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<List<Match>> getAllMatchs() {
        List<Match> matchs = matchService.getAllMatchs();
        return new ResponseEntity<>(matchs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Integer id) {
        Match match = matchService.getMatchById(id);
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @GetMapping("/saison/{saisonId}")
    public ResponseEntity<List<Match>> getMatchsBySaison(@PathVariable Integer saisonId) {
        List<Match> matchs = matchService.getMatchsBySaison(saisonId);
        return new ResponseEntity<>(matchs, HttpStatus.OK);
    }

    @GetMapping("/club/{clubId}/saison/{saisonId}")
    public ResponseEntity<List<Match>> getMatchsByClubAndSaison(
            @PathVariable Integer clubId,
            @PathVariable Integer saisonId) {
        List<Match> matchs = matchService.getMatchsByClubAndSaison(clubId, saisonId);
        return new ResponseEntity<>(matchs, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Match> createMatch(@RequestBody Match match) {
        Match newMatch = matchService.createMatch(match);
        return new ResponseEntity<>(newMatch, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> updateMatch(@PathVariable Integer id, @RequestBody Match match) {
        Match updatedMatch = matchService.updateMatch(id, match);
        return new ResponseEntity<>(updatedMatch, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Integer id) {
        matchService.deleteMatch(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/resultat")
    public ResponseEntity<Match> enregistrerResultat(@PathVariable Integer id, @RequestBody ResultatMatch resultatMatch) {
        // S'assurer que l'ID du match est correctement défini
        resultatMatch.setMatchId(id);
        Match matchMisAJour = matchService.enregistrerResultat(resultatMatch);
        return new ResponseEntity<>(matchMisAJour, HttpStatus.OK);
    }

    @PostMapping("/saison/{saisonId}/generer")
    public ResponseEntity<List<Match>> genererCalendrierSaison(@PathVariable Integer saisonId) {
        List<Match> matchsGeneres = matchService.genererCalendrierSaison(saisonId);
        return new ResponseEntity<>(matchsGeneres, HttpStatus.CREATED);
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<Match> updateMatchStatus(
            @PathVariable Integer id,
            @RequestParam Boolean estJoue) {
        Match match = matchService.updateMatchStatus(id, estJoue);
        return new ResponseEntity<>(match, HttpStatus.OK);
    }
    @PostMapping("/{id}/goals")
    public ResponseEntity<Match> addGoal(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> goalData) {

        Integer joueurId = (Integer) goalData.get("joueurId");
        Integer saisonId = (Integer) goalData.get("saisonId");
        String equipe = (String) goalData.get("equipe"); // "domicile" ou "exterieur"
        Integer minute = (Integer) goalData.get("minute");

        Match match = matchService.addGoal(id, joueurId, saisonId, equipe, minute);
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

}
