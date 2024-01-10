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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String idFactores;
    private Double redeterminacionFac;
    private Double adecuacionProvisoria;
    private Double factorItem;
    private Double incidencia;

    public Factores() {
    }

}
