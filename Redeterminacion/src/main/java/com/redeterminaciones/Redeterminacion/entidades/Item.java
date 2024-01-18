package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String numeroItem;
    private String descripcion;
    private String unidad;
    private Double cantidad;
    @OneToOne
    private Remanentes remanentes;
    @OneToOne
    private Factores factores;
    @OneToOne
    private Montos montos;

    public Item() {
    }

}
