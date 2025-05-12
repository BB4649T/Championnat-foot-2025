package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classement {
    private Integer rang;
    private Integer clubId;
    private String clubNom;
    private Integer points;
    private Integer butsMarques;
    private Integer butsEncaisses;
    private Integer differenceButs;
    private Integer cleanSheets;
}

