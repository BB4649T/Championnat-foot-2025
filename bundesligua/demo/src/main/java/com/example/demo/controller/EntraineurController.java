package com.example.demo.controller;

import com.example.demo.model.Entraineur;
import com.example.demo.service.EntraineurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entraineurs")
public class EntraineurController {

    private final EntraineurService entraineurService;

    @Autowired
    public EntraineurController(EntraineurService entraineurService) {
        this.entraineurService = entraineurService;
    }

    @GetMapping
    public ResponseEntity<List<Entraineur>> getAllEntraineurs() {
        List<Entraineur> entraineurs = entraineurService.getAllEntraineurs();
        return new ResponseEntity<>(entraineurs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entraineur> getEntraineurById(@PathVariable Integer id) {
        Entraineur entraineur = entraineurService.getEntraineurById(id);
        return new ResponseEntity<>(entraineur, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Entraineur> createEntraineur(@RequestBody Entraineur entraineur) {
        Entraineur newEntraineur = entraineurService.createEntraineur(entraineur);
        return new ResponseEntity<>(newEntraineur, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entraineur> updateEntraineur(@PathVariable Integer id, @RequestBody Entraineur entraineur) {
        Entraineur updatedEntraineur = entraineurService.updateEntraineur(id, entraineur);
        return new ResponseEntity<>(updatedEntraineur, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntraineur(@PathVariable Integer id) {
        entraineurService.deleteEntraineur(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/nationalite/{nationalite}")
    public ResponseEntity<List<Entraineur>> getEntraineursByNationalite(@PathVariable String nationalite) {
        List<Entraineur> entraineurs = entraineurService.getEntraineursByNationalite(nationalite);
        return new ResponseEntity<>(entraineurs, HttpStatus.OK);
    }
}
