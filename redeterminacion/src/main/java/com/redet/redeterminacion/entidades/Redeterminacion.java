package com.redet.redeterminacion.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
    private Integer idRedet;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate mesSolicitud;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate mesSolictudAnterior;
    private Double variacionReferencia;
    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] resumenRedet;
}
