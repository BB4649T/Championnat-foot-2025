package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Joueur {
    private Integer id;
    private String nom;
    private Integer numero;
    private String poste;
    private String nationalite;
    private Integer age;
    private Integer clubId;

    private String clubNom;
}

