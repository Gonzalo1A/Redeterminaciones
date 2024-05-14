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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private int indice;
    private Float porcentajeIncidencia;

    public IncidenciaFactor() {
    }
    
   
}
