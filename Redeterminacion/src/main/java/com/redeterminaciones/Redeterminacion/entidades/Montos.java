package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Montos {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idMontos;
    private Double nuevoMonto;
    private Double redeterminacionMonto;
    private Double adecuacionProvisoriaMonto;
    private Double incremento;

    public Montos() {
    }

}
