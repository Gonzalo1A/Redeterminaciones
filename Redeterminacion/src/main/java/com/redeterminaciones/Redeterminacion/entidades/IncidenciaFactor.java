package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
public class IncidenciaFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private int indice;
    private Float porcentajeIncidencia;

    public IncidenciaFactor() {
    }
    
    public IncidenciaFactor(int indice, Float porcentajeIncidencia) {
        this.indice = indice;
        this.porcentajeIncidencia = porcentajeIncidencia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public Float getPorcentajeIncidencia() {
        return porcentajeIncidencia;
    }

    public void setPorcentajeIncidencia(Float porcentajeIncidencia) {
        this.porcentajeIncidencia = porcentajeIncidencia;
    }
}
