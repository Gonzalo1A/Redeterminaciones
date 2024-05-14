package com.redeterminaciones.Redeterminacion.entidades;

import java.util.List;

public class DatosRecibidos {

    private String nombreObra;
    private List<ValoresIncidenciaLista> listaDatos;

    public String getNombreObra() {
        return nombreObra;
    }

    public void setNombreObra(String nombreObra) {
        this.nombreObra = nombreObra;
    }

    public List<ValoresIncidenciaLista> getListaDatos() {
        return listaDatos;
    }

    public void setListaDatos(List<ValoresIncidenciaLista> listaDatos) {
        this.listaDatos = listaDatos;
    }

    public DatosRecibidos() {
    }

}
