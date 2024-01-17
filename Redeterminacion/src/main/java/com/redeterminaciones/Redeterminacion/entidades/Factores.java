package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Factores {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idFactores;
    private Double adecuacionProvisoriaFactor;
    private Double itemFactor;
    private Double incidencia;

    public Factores() {
    }
}
