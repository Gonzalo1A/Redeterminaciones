package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
public class Redeterminacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate mesSolicitud;
    private Double variacionReferencia;

}
