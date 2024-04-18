package com.redeterminaciones.Redeterminacion.entidades;

import com.redeterminaciones.Redeterminacion.enumeraciones.Rubros;
import com.redeterminaciones.Redeterminacion.enumeraciones.TipoDeRedeterminaciones;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Entity
@Data
public class Obra {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String nombre;
    private String total;
    private Date fechaPresentacionObra;
    private Date fechaDeContrato;
    private Date fechaDeReeplanteo;
    private Double porcentajeDeAnticipo;
    private int diasPlazoDeObra;
    private Date fechaDeFinalizacion;
    private String comitente;
    @Enumerated(EnumType.STRING)
    private TipoDeRedeterminaciones tipoDeRedet;
//    @OneToOne
//    private ComputoYPresupuesto computoYPresupuesto;
    @OneToMany
    private List<Item> items;


    public Obra() {

    }
}
