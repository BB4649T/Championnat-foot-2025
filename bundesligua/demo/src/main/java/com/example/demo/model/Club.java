package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Club {
    private Integer id;
    private String nom;
    private String acronyme;
    private Integer anneeCreation;
    private String nomStade;
    private Integer championnatId;

    private String championnatNom;
}

