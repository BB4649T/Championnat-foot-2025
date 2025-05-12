package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Entraineur;
import com.example.demo.repository.EntraineurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntraineurService {

    private final EntraineurRepository entraineurRepository;
    private final ClubService clubService;

    @Autowired
    public EntraineurService(EntraineurRepository entraineurRepository, ClubService clubService) {
        this.entraineurRepository = entraineurRepository;
        this.clubService = clubService;
    }

    public List<Entraineur> getAllEntraineurs() {
        return entraineurRepository.findAll();
    }

    public Entraineur getEntraineurById(Integer id) {
        return entraineurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entraineur non trouvé avec l'id: " + id));
    }

    public Entraineur getEntraineurByClubId(Integer clubId) {
        // Vérifier si le club existe
        clubService.getClubById(clubId);

        return entraineurRepository.findByClubId(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Entraineur non trouvé pour le club avec l'id: " + clubId));
    }

    public Entraineur createEntraineur(Entraineur entraineur) {
        // Vérifier si le club existe
        clubService.getClubById(entraineur.getClubId());

        // Vérifier si un entraineur existe déjà pour ce club
        entraineurRepository.findByClubId(entraineur.getClubId())
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Un entraineur existe déjà pour ce club");
                });

        return entraineurRepository.save(entraineur);
    }

    public Entraineur updateEntraineur(Integer id, Entraineur entraineurDetails) {
        Entraineur entraineur = getEntraineurById(id);
        entraineur.setNom(entraineurDetails.getNom());
        entraineur.setNationalite(entraineurDetails.getNationalite());

        if (entraineurDetails.getClubId() != null &&
                !entraineurDetails.getClubId().equals(entraineur.getClubId())) {
            // Vérifier si le club existe
            clubService.getClubById(entraineurDetails.getClubId());

            // Vérifier si un entraineur existe déjà pour ce club
            entraineurRepository.findByClubId(entraineurDetails.getClubId())
                    .ifPresent(e -> {
                        throw new IllegalArgumentException("Un entraineur existe déjà pour ce club");
                    });

            entraineur.setClubId(entraineurDetails.getClubId());
        }

        return entraineurRepository.save(entraineur);
    }

    public void deleteEntraineur(Integer id) {
        Entraineur entraineur = getEntraineurById(id);
        entraineurRepository.deleteById(id);
    }
    public List<Entraineur> getEntraineursByNationalite(String nationalite) {
        return entraineurRepository.findByNationalite(nationalite);
    }

}

