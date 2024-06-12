package com.redeterminaciones.Redeterminacion.entidades;

import com.redeterminaciones.Redeterminacion.enumeraciones.TipoDeRedeterminaciones;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
public class Obra {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String nombre;
    private String total;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaPresentacionObra;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaDeContrato;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaDeReeplanteo;
    private Double porcentajeDeAnticipo;
    private int diasPlazoDeObra;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaDeFinalizacion;
    private String comitente;
    @Enumerated(EnumType.STRING)
    private TipoDeRedeterminaciones tipoDeRedet;
    @OneToMany
    private List<Item> items;


  
}
