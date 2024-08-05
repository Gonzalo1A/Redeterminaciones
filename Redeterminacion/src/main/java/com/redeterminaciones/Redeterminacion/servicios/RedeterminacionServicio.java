package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.Redeterminacion;
import com.redeterminaciones.Redeterminacion.repositorios.RedeterminacionRepositorio;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedeterminacionServicio {

    @Autowired
    private RedeterminacionRepositorio redeterminacionRepositorio;

    @Transactional
    public Redeterminacion crearRedeterminacion(LocalDate mesSolicitud, LocalDate mesOferta) {
        Redeterminacion redeterminacion = new Redeterminacion();
        redeterminacion.setMesSolicitud(mesSolicitud);
        redeterminacion.setMesSolictudAnterior(mesOferta);
        redeterminacionRepositorio.save(redeterminacion);
        return redeterminacion;
    }

    @Transactional
    public void modificar(Double remanente, String id) {
        Optional<Redeterminacion> res = redeterminacionRepositorio.findById(id);
        if (res.isPresent()) {
            Redeterminacion redeterminacionMod = res.get();
            redeterminacionMod.setVariacionReferencia(remanente);
            redeterminacionRepositorio.save(redeterminacionMod);
        }
    }

    public Redeterminacion buscarRedeterminacion(String id) {
        return redeterminacionRepositorio.getReferenceById(id);
    }

    public List<Redeterminacion> listaRedeterminacionesPorObra(Obra obra) {
        return obra.getRedeterminaciones();
    }

    @Transactional
    public void eliminarRedeterminacion(String id) {
        redeterminacionRepositorio.deleteById(id);
    }

    public Double calcularVR(Double ponderadorTotal, Double valMesBase, Double valMesAnterior) {
        return (valMesAnterior / valMesBase) * ponderadorTotal;
    }

}
