package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity
public class IndiceMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Double valor;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "MM-yyyy")
    private Date fecha;
}
