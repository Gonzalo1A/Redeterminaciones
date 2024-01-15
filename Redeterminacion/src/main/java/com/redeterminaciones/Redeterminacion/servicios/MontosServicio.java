package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.Montos;
import com.redeterminaciones.Redeterminacion.repositorios.MontosRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MontosServicio {

    @Autowired
    private MontosRepositorio montosRepositorio;

    public void crearMontos(String idMontos, Double montoRedetermincacion, Double incremento, Double nuevoMonto, Double adecuacionProvisoria) {
        Montos montos = new Montos();
        montos.setIdMontos(idMontos);
        montos.setIncremento(incremento);
        montos.setRedeterminacionMonto(montoRedetermincacion);
        montos.setNuevoMonto(nuevoMonto);
        montos.setAdecuacionProvisoriaMonto(adecuacionProvisoria);
        montosRepositorio.save(montos);
    }

    public List<Montos> listarMontos() {
        return montosRepositorio.findAll();
    }

    public void modificarMontos(String idMontos, Double montoRedetermincacion, Double incremento, Double nuevoMonto, Double adecuacionProvisoria) {
        Optional<Montos> respuesta = montosRepositorio.findById(idMontos);
        if (respuesta.isPresent()) {
            Montos montos = respuesta.get();
            montos.setIdMontos(idMontos);
            montos.setIncremento(incremento);
            montos.setRedeterminacionMonto(montoRedetermincacion);
            montos.setNuevoMonto(nuevoMonto);
            montos.setAdecuacionProvisoriaMonto(adecuacionProvisoria);
            montosRepositorio.save(montos);
        }
    }

    public Montos getOne(String id) {
        return montosRepositorio.getOne(id);
    }

    public void eliminarMontos(String id) {
        montosRepositorio.deleteById(id);
    }
}
