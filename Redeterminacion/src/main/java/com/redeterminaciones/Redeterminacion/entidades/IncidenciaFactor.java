package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class IncidenciaFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private int indice;
    private Float porcentajeIncidencia;

    public IncidenciaFactor() {
    }
    
    public IncidenciaFactor(int indice, Float porcentajeIncidencia) {
        this.indice = indice;
        this.porcentajeIncidencia = porcentajeIncidencia;
    }
}
