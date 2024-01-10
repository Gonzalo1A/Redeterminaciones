package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
@Data
@Entity
public class ComputoYPresupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String idComputoYPresupuesto;
    
}
