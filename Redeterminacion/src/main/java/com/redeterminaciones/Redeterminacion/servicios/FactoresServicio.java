package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.Factores;
import com.redeterminaciones.Redeterminacion.repositorios.FactoresRepositorio;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FactoresServicio {

    @Autowired
    private FactoresRepositorio factoresRepositorio;

    @Transactional
    public void crearFactor(Double adecuacionMonetaria, Double itemFactor, Double incidencia) {
        Factores factores = new Factores();
        factores.setAdecuacionProvisoriaFactor(adecuacionMonetaria);
        factores.setItemFactor(itemFactor);
        factores.setIncidencia(incidencia);
        factoresRepositorio.save(factores);
    }

    public List<Factores> listarFactores() {
        return factoresRepositorio.findAll();
    }

    @Transactional
    public void modificarFactores(String idFactores, Double adecuacionMonetaria, Double itemFactor, Double incidencia) {
        Optional<Factores> respuesta = factoresRepositorio.findById(idFactores);
        if (respuesta.isPresent()) {
            Factores factores = respuesta.get();
            factores.setAdecuacionProvisoriaFactor(adecuacionMonetaria);
            factores.setItemFactor(itemFactor);
            factores.setIncidencia(incidencia);
            factoresRepositorio.save(factores);
        }
    }

    public Factores getOne(String id) {
        return factoresRepositorio.getOne(id);
    }

    @Transactional
    public void eliminarFactores(String id) {
        factoresRepositorio.deleteById(id);
    }
}
