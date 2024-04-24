package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class IncidenciaFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @OneToOne
    private IOP indice;
    private Float porcentajeIncidencia;

}
