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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String idRemanentes;
    private Double teorico;
    private Double real;
    private Double valorMin;

    public Remanentes() {
    }

}
