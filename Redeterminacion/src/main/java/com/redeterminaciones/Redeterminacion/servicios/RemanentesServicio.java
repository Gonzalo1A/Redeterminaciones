package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.Remanentes;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.redeterminaciones.Redeterminacion.repositorios.RemanentesRepositorio;

@Service
public class RemanentesServicio {

    @Autowired
    private RemanentesRepositorio remanentesRepositorio;

    public void crearRemanentes(Double remanenteTeorico, Double remanenteReal, Double remanenteValorMin) {
        Remanentes remanentes = new Remanentes();
        remanentes.setRemanenteReal(remanenteReal);
        remanentes.setRemanenteTeorico(remanenteTeorico);
        remanentes.setRemanenteValorMin(remanenteValorMin);
        remanentesRepositorio.save(remanentes);
    }

    public List<Remanentes> listarRemanentes() {
        return remanentesRepositorio.findAll();
    }

    public void modificarRemanentes(String idRemanentes, Double remanenteTeorico, Double remanenteReal, Double remanenteValorMin) {
        Optional<Remanentes> respuesta = remanentesRepositorio.findById(idRemanentes);
        if (respuesta.isPresent()) {
            Remanentes remanentes = respuesta.get();
            remanentes.setRemanenteReal(remanenteReal);
            remanentes.setRemanenteTeorico(remanenteTeorico);
            remanentes.setRemanenteValorMin(remanenteValorMin);
            remanentesRepositorio.save(remanentes);
        }
    }

    public Remanentes getOne(String id) {
        return remanentesRepositorio.getOne(id);
    }

    public void eliminarFactores(String id) {
        remanentesRepositorio.deleteById(id);
    }
}
