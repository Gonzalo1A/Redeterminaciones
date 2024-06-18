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

@Data
@Entity
public class ValorMes implements Comparable<ValorMes> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Double valor;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "MM-yyyy")
    private LocalDate fecha;

    @Override
    public int compareTo(ValorMes other) {
        return this.fecha.compareTo(other.fecha);
    }
}
