package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Saison;
import com.example.demo.repository.SaisonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SaisonService {

    private final SaisonRepository saisonRepository;

    @Autowired
    public SaisonService(SaisonRepository saisonRepository) {
        this.saisonRepository = saisonRepository;
    }

    public List<Saison> getAllSaisons() {
        return saisonRepository.findAll();
    }

    public Saison getSaisonById(Integer id) {
        return saisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'id: " + id));
    }

    public Optional<Saison> getCurrentSaison() {
        return saisonRepository.findCurrentSaison();
    }

    public Saison createSaison(Saison saison) {
        // Par défaut, une nouvelle saison n'est pas terminée
        saison.setEstTerminee(false);

        // Vérifier s'il existe déjà une saison en cours
        Optional<Saison> currentSaison = saisonRepository.findCurrentSaison();
        if (currentSaison.isPresent()) {
            throw new IllegalStateException("Il existe déjà une saison en cours: "
                    + currentSaison.get().getNom());
        }

        return saisonRepository.save(saison);
    }

    @Transactional
    public void terminerSaison(Integer id) {
        Saison saison = getSaisonById(id);

        if (saison.isEstTerminee()) {
            throw new IllegalStateException("La saison est déjà terminée");
        }

        saisonRepository.terminerSaison(id);
    }
}

