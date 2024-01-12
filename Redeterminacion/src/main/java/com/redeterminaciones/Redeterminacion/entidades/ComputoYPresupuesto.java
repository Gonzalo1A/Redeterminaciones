package com.redeterminaciones.Redeterminacion.entidades;

import com.redeterminaciones.Redeterminacion.enumeraciones.Rubros;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table
public class ComputoYPresupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String idComputoYPresupuesto;
    @Enumerated(EnumType.STRING)
    private Rubros rubro;
    private Double subTotales;
    private Double total;
    @OneToOne
    private Obra obra;
}
