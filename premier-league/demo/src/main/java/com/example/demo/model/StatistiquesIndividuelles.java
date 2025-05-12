package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesIndividuelles {
    private Integer id;
    private Integer joueurId;
    private Integer saisonId;
    private Integer butsMarques;
    private Integer dureeJeu;

    private String joueurNom;
    private String saisonNom;
}

