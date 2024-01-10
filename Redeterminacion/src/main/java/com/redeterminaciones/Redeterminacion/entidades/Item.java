package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String numeroItem;
    private String descripcion;
    private String unidad;
    private Double cantidad;
    private Remanentes remanentes;
    private Factores factores;
    private Montos montos;

    public Item() {
    }

}
