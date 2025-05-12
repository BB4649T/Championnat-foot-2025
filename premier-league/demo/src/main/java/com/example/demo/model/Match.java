package com.example.demo.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private Integer id;
    private Integer clubDomicileId;
    private Integer clubExterieurId;
    private String stade;
    private LocalDateTime dateHeure;
    private Integer scoreDomicile;
    private Integer scoreExterieur;
    private Integer saisonId;
    private boolean estJoue;

    private String clubDomicileNom;
    private String clubExterieurNom;
    private String saisonNom;
}


