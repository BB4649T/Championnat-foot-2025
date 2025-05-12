package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultatMatch {
    private Integer matchId;
    private Integer scoreDomicile;
    private Integer scoreExterieur;
}

