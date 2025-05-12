package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entraineur {
    private Integer id;
    private String nom;
    private String nationalite;
    private Integer clubId;

    private String clubNom;
}

