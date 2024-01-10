package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Remanentes {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idRemanentes;
    private Double remanenteTeorico;
    private Double remanenteReal;
    private Double remanenteValorMin;

    public Remanentes() {
    }
}
