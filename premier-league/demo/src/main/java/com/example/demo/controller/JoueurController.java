package com.example.demo.controller;

import com.example.demo.model.Joueur;
import com.example.demo.model.StatistiquesIndividuelles;
import com.example.demo.service.JoueurService;
import com.example.demo.service.StatistiquesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/joueurs")
public class JoueurController {

    private final JoueurService joueurService;
    private final StatistiquesService statistiquesService;

    @Autowired
    public JoueurController(JoueurService joueurService, StatistiquesService statistiquesService) {
        this.joueurService = joueurService;
        this.statistiquesService = statistiquesService;
    }

    @GetMapping
    public ResponseEntity<List<Joueur>> getAllJoueurs() {
        List<Joueur> joueurs = joueurService.getAllJoueurs();
        return new ResponseEntity<>(joueurs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Joueur> getJoueurById(@PathVariable Integer id) {
        Joueur joueur = joueurService.getJoueurById(id);
        return new ResponseEntity<>(joueur, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Joueur> createJoueur(@RequestBody Joueur joueur) {
        Joueur newJoueur = joueurService.createJoueur(joueur);
        return new ResponseEntity<>(newJoueur, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Joueur> updateJoueur(@PathVariable Integer id, @RequestBody Joueur joueur) {
        Joueur updatedJoueur = joueurService.updateJoueur(id, joueur);
        return new ResponseEntity<>(updatedJoueur, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJoueur(@PathVariable Integer id) {
        joueurService.deleteJoueur(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{joueurId}/statistiques/{saisonId}")
    public ResponseEntity<StatistiquesIndividuelles> getStatistiquesByJoueur(
            @PathVariable Integer joueurId,
            @PathVariable Integer saisonId) {
        StatistiquesIndividuelles statistiques = statistiquesService.getStatistiquesIndividuellesByJoueurAndSaison(joueurId, saisonId);
        return new ResponseEntity<>(statistiques, HttpStatus.OK);
    }

    @GetMapping("/poste/{poste}")
    public ResponseEntity<List<Joueur>> getJoueursByPoste(@PathVariable String poste) {
        List<Joueur> joueurs = joueurService.getJoueursByPoste(poste);
        return new ResponseEntity<>(joueurs, HttpStatus.OK);
    }
    @GetMapping("/filter")
    public ResponseEntity<List<Joueur>> getJoueursByFilter(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) Integer clubId,
            @RequestParam(required = false) Integer ageMin,
            @RequestParam(required = false) Integer ageMax) {
        List<Joueur> joueurs = joueurService.getJoueursByFilter(nom, clubId, ageMin, ageMax);
        return new ResponseEntity<>(joueurs, HttpStatus.OK);
    }

}
