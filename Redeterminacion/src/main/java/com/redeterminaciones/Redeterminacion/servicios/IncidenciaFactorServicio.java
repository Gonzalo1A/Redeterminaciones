package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.IOP;
import com.redeterminaciones.Redeterminacion.entidades.IncidenciaFactor;
import com.redeterminaciones.Redeterminacion.repositorios.IncidenciaFactorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncidenciaFactorServicio {
    
    @Autowired
    private IncidenciaFactorRepositorio incidenciaFactorRepositorio;
    
    public void crearIncidenciaFactor(Float porcentaje, IOP factorReferencia) {
        IncidenciaFactor incidenciaFactor = new IncidenciaFactor();
        incidenciaFactor.setIndice(factorReferencia);
        incidenciaFactor.setPorcentajeIncidencia(porcentaje);
        incidenciaFactorRepositorio.save(incidenciaFactor);
    }
    
    public void modificarIncidenciaFactor(Float porcentaje, IOP factorReferencia) {
        IncidenciaFactor incidenciaFactor = new IncidenciaFactor();
        incidenciaFactor.setIndice(factorReferencia);
        incidenciaFactor.setPorcentajeIncidencia(porcentaje);
        incidenciaFactorRepositorio.save(incidenciaFactor);
    }
    
    public IncidenciaFactor buscarIncidenciaFactor(String id) {
        return incidenciaFactorRepositorio.getReferenceById(id);
    }
    
    public void eliminarIncidenteFactor(String id) {
        incidenciaFactorRepositorio.deleteById(id);
    }
}
