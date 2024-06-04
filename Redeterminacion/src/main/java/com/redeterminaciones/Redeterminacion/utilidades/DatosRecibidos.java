package com.redeterminaciones.Redeterminacion.utilidades;

import java.util.List;

public class DatosRecibidos {

    private String nombreObra;
    private List<ConjuntoIdValorFecha> listaDatos;

    public String getNombreObra() {
        return nombreObra;
    }

    public void setNombreObra(String nombreObra) {
        this.nombreObra = nombreObra;
    }

    public List<ConjuntoIdValorFecha> getListaDatos() {
        return listaDatos;
    }

    public void setListaDatos(List<ConjuntoIdValorFecha> listaDatos) {
        this.listaDatos = listaDatos;
    }

    public DatosRecibidos() {
    }

}
