package com.redeterminaciones.Redeterminacion.entidades;

import com.poiji.annotation.ExcelCellName;
import com.poiji.annotation.ExcelSheet;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@ExcelSheet("Items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @ExcelCellName("Nro")
    private String numeroItem;
    @ExcelCellName("Descripcion")
    private String descripcion;
    @ExcelCellName("Unidad")
    private String unidad;
    @ExcelCellName("Cantidad")
    private Double cantidad;
    private Double subTotal;
    @ExcelCellName("Precio Unitario")
    private Double precioUnitario;
    @OneToOne
    private Remanentes remanentes;
    @OneToOne
    private Factores factores;
    @OneToOne
    private Montos montos;

    public Item() {
    }

    public Item(String id, String numeroItem, String descripcion, String unidad, Double cantidad, Double subTotal, Double precioUnitario, Remanentes remanentes, Factores factores, Montos montos) {
        this.id = id;
        this.numeroItem = numeroItem;
        this.descripcion = descripcion;
        this.unidad = unidad;
        this.cantidad = cantidad;
        this.subTotal = subTotal;
        this.precioUnitario = precioUnitario;
        this.remanentes = remanentes;
        this.factores = factores;
        this.montos = montos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    

    public String getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(String numeroItem) {
        this.numeroItem = numeroItem;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Remanentes getRemanentes() {
        return remanentes;
    }

    public void setRemanentes(Remanentes remanentes) {
        this.remanentes = remanentes;
    }

    public Factores getFactores() {
        return factores;
    }

    public void setFactores(Factores factores) {
        this.factores = factores;
    }

    public Montos getMontos() {
        return montos;
    }

    public void setMontos(Montos montos) {
        this.montos = montos;
    }

}
