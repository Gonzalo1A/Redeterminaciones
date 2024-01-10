package com.redeterminaciones.Redeterminacion.entidades;

import com.redeterminaciones.Redeterminacion.enumeraciones.TipoDeRedeterminaciones;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.Data;

@Entity
@Data
public class Obra {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String idObra;
    
    private String nombre;
    private Double total;
    private Date fechaPresentacionObra;
    private Date fechaDeContrato;
    private Date fechaDeReeplanteo;
    private Double porcentajeDeAnticipo;
    private int diasPazoDeObra;
    private Date fechaDeFinalizacion;
    @Enumerated(EnumType.STRING)
    private TipoDeRedeterminaciones tipoDeRedet;

    public Obra() {

    }
}
