package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesCollectives {
    private Integer id;
    private Integer clubId;
    private Integer saisonId;
    private Integer points;
    private Integer butsMarques;
    private Integer butsEncaisses;
    private Integer differenceButs;
    private Integer cleanSheets;

    private String clubNom;
    private String saisonNom;
}

