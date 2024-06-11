package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class ValorMes implements Comparable<ValorMes> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Double valor;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "MM-yyyy")
    private LocalDate fecha;

    public ValorMes(Double valor, LocalDate fecha) {
        this.valor = valor;
        this.fecha = fecha;
    }

    public ValorMes() {
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    @Override
    public int compareTo(ValorMes other) {
        return this.fecha.compareTo(other.fecha);
    }
}
