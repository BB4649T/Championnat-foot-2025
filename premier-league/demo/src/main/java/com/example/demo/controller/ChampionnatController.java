package com.example.demo.controller;

import com.example.demo.model.Championnat;
import com.example.demo.model.Classement;
import com.example.demo.model.Club;
import com.example.demo.service.ChampionnatService;
import com.example.demo.service.ClubService;
import com.example.demo.service.StatistiquesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/championnats")
public class ChampionnatController {

    private final ChampionnatService championnatService;
    private final ClubService clubService;
    private final StatistiquesService statistiquesService;

    @Autowired
    public ChampionnatController(ChampionnatService championnatService,
                                 ClubService clubService,
                                 StatistiquesService statistiquesService) {
        this.championnatService = championnatService;
        this.clubService = clubService;
        this.statistiquesService = statistiquesService;
    }

    @GetMapping
    public ResponseEntity<List<Championnat>> getAllChampionnats() {
        List<Championnat> championnats = championnatService.getAllChampionnats();
        return new ResponseEntity<>(championnats, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Championnat> getChampionnatById(@PathVariable Integer id) {
        Championnat championnat = championnatService.getChampionnatById(id);
        return new ResponseEntity<>(championnat, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Championnat> createChampionnat(@RequestBody Championnat championnat) {
        Championnat newChampionnat = championnatService.createChampionnat(championnat);
        return new ResponseEntity<>(newChampionnat, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Championnat> updateChampionnat(@PathVariable Integer id, @RequestBody Championnat championnat) {
        Championnat updatedChampionnat = championnatService.updateChampionnat(id, championnat);
        return new ResponseEntity<>(updatedChampionnat, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChampionnat(@PathVariable Integer id) {
        championnatService.deleteChampionnat(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/clubs")
    public ResponseEntity<List<Club>> getClubsByChampionnat(@PathVariable Integer id) {
        List<Club> clubs = clubService.getClubsByChampionnatId(id);
        return new ResponseEntity<>(clubs, HttpStatus.OK);
    }

    @GetMapping("/{championnatId}/classement/{saisonId}")
    public ResponseEntity<List<Classement>> getClassement(
            @PathVariable Integer championnatId,
            @PathVariable Integer saisonId) {
        List<Classement> classement = statistiquesService.getClassementByChampionnatAndSaison(championnatId, saisonId);
        return new ResponseEntity<>(classement, HttpStatus.OK);
    }
}
