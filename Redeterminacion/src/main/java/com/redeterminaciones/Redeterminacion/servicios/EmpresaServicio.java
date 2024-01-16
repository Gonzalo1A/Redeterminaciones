package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.ComputoYPresupuesto;
import com.redeterminaciones.Redeterminacion.entidades.Empresa;
import com.redeterminaciones.Redeterminacion.repositorios.EmpresaRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaServicio {

    @Autowired
    private EmpresaRepositorio empresaRepositorio;

    @Transactional
    public void crearEmpresa(String nombre, Double oferta, ComputoYPresupuesto computoYPresupuesto) {
        Empresa nueva = new Empresa();
        nueva.setNombre(nombre);
        nueva.setOferta(oferta);
        nueva.setComputoYPresupuesto(computoYPresupuesto);
        empresaRepositorio.save(nueva);
    }

    @Transactional
    public void modificarEmpresa(String idEmpresa, String nombre, Double oferta, ComputoYPresupuesto computoYPresupuesto) {
        Empresa empresaModificar = empresaRepositorio.getById(idEmpresa);
        empresaModificar.setNombre(nombre);
        empresaModificar.setOferta(oferta);
        empresaModificar.setComputoYPresupuesto(computoYPresupuesto);
        empresaRepositorio.save(empresaModificar);
    }

    public Empresa buscarEmpresaPorNombre(String nombre) {
        return empresaRepositorio.buscarObraPorNombre(nombre);
    }

    @Transactional
    public void eliminarEmpresaPorNombre(String nombre) {
        Empresa empresaEliminar = empresaRepositorio.buscarObraPorNombre(nombre);
        empresaRepositorio.delete(empresaEliminar);
    }
}
