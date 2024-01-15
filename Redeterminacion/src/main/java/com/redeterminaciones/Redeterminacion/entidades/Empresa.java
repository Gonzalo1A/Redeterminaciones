package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Empresa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String idEmpresas;
    
    private String nombre;
    private Double oferta;
    @OneToOne
    private ComputoYPresupuesto computoYPresupuesto;
    

}
