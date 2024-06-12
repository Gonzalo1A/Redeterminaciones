package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Data;

@Data
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroItem;
    private String descripcion;
    private String unidad;
    private Double cantidad;
    private Double subTotal;
    private Double precioUnitario;
    private boolean rubro;
    @OneToMany
    private List<ValorMes> avanceTeorico;
    @OneToMany
    private List<IncidenciaFactor> incidenciaFactores;
    private Double incidenciaItem;
    @OneToMany
    private List<AvanceObraReal> avanceObraReal;

}
