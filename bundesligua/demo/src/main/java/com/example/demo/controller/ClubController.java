package com.example.demo.controller;

import com.example.demo.model.Club;
import com.example.demo.model.Entraineur;
import com.example.demo.model.Joueur;
import com.example.demo.model.StatistiquesCollectives;
import com.example.demo.service.ClubService;
import com.example.demo.service.EntraineurService;
import com.example.demo.service.JoueurService;
import com.example.demo.service.StatistiquesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    private final ClubService clubService;
    private final JoueurService joueurService;
    private final EntraineurService entraineurService;
    private final StatistiquesService statistiquesService;

    @Autowired
    public ClubController(ClubService clubService,
                          JoueurService joueurService,
                          EntraineurService entraineurService,
                          StatistiquesService statistiquesService) {
        this.clubService = clubService;
        this.joueurService = joueurService;
        this.entraineurService = entraineurService;
        this.statistiquesService = statistiquesService;
    }

    @GetMapping
    public ResponseEntity<List<Club>> getAllClubs() {
        List<Club> clubs = clubService.getAllClubs();
        return new ResponseEntity<>(clubs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Club> getClubById(@PathVariable Integer id) {
        Club club = clubService.getClubById(id);
        return new ResponseEntity<>(club, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Club> createClub(@RequestBody Club club) {
        Club newClub = clubService.createClub(club);
        return new ResponseEntity<>(newClub, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Club> updateClub(@PathVariable Integer id, @RequestBody Club club) {
        Club updatedClub = clubService.updateClub(id, club);
        return new ResponseEntity<>(updatedClub, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable Integer id) {
        clubService.deleteClub(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/joueurs")
    public ResponseEntity<List<Joueur>> getJoueursByClub(@PathVariable Integer id) {
        List<Joueur> joueurs = joueurService.getJoueursByClubId(id);
        return new ResponseEntity<>(joueurs, HttpStatus.OK);
    }

    @GetMapping("/{id}/entraineur")
    public ResponseEntity<Entraineur> getEntraineurByClub(@PathVariable Integer id) {
        Entraineur entraineur = entraineurService.getEntraineurByClubId(id);
        return new ResponseEntity<>(entraineur, HttpStatus.OK);
    }

    @GetMapping("/{clubId}/statistiques/{saisonId}")
    public ResponseEntity<StatistiquesCollectives> getStatistiquesByClub(
            @PathVariable Integer clubId,
            @PathVariable Integer saisonId) {
        StatistiquesCollectives statistiques = statistiquesService.getStatistiquesCollectivesByClubAndSaison(clubId, saisonId);
        return new ResponseEntity<>(statistiques, HttpStatus.OK);
    }
    @PutMapping("/{id}/joueurs")
    public ResponseEntity<List<Joueur>> updateJoueursFromClub(
            @PathVariable Integer id,
            @RequestBody List<Integer> joueursIds) {
        List<Joueur> joueurs = clubService.updateJoueursFromClub(id, joueursIds);
        return new ResponseEntity<>(joueurs, HttpStatus.OK);
    }

}
