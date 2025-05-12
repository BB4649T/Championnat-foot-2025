package com.example.demo.controller;

import com.example.demo.model.Saison;
import com.example.demo.service.SaisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/saisons")
public class SaisonController {

    private final SaisonService saisonService;

    @Autowired
    public SaisonController(SaisonService saisonService) {
        this.saisonService = saisonService;
    }

    @GetMapping
    public ResponseEntity<List<Saison>> getAllSaisons() {
        List<Saison> saisons = saisonService.getAllSaisons();
        return new ResponseEntity<>(saisons, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Saison> getSaisonById(@PathVariable Integer id) {
        Saison saison = saisonService.getSaisonById(id);
        return new ResponseEntity<>(saison, HttpStatus.OK);
    }

    @GetMapping("/courante")
    public ResponseEntity<Saison> getCurrentSaison() {
        Optional<Saison> saisonOptional = saisonService.getCurrentSaison();
        return saisonOptional
                .map(saison -> new ResponseEntity<>(saison, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Saison> createSaison(@RequestBody Saison saison) {
        Saison newSaison = saisonService.createSaison(saison);
        return new ResponseEntity<>(newSaison, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/terminer")
    public ResponseEntity<Void> terminerSaison(@PathVariable Integer id) {
        saisonService.terminerSaison(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
