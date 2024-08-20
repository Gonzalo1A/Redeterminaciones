package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.IncidenciaFactor;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.Redeterminacion;
import com.redeterminaciones.Redeterminacion.repositorios.RedeterminacionRepositorio;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedeterminacionServicio {

    @Autowired
    private RedeterminacionRepositorio redeterminacionRepositorio;
    @Autowired
    private IOPServicio iopServ;

    @Transactional
    public Redeterminacion crearRedeterminacion(LocalDate mesSolicitud, LocalDate mesOferta) {
        Redeterminacion redeterminacion = new Redeterminacion();
        redeterminacion.setMesSolicitud(mesSolicitud);
        redeterminacion.setMesSolictudAnterior(mesOferta);
        redeterminacionRepositorio.save(redeterminacion);
        return redeterminacion;
    }

    @Transactional
    public void modificar(Double remanente, Integer id) {
        Optional<Redeterminacion> res = redeterminacionRepositorio.findById(id);
        if (res.isPresent()) {
            Redeterminacion redeterminacionMod = res.get();
            redeterminacionMod.setVariacionReferencia(remanente);
            redeterminacionRepositorio.save(redeterminacionMod);
        }
    }

    public Redeterminacion buscarRedeterminacion(Integer id) {
        return redeterminacionRepositorio.getReferenceById(id);
    }

    public List<Redeterminacion> listaRedeterminacionesPorObra(Obra obra) {
        return obra.getRedeterminaciones();
    }

    @Transactional
    public void eliminarRedeterminacion(Integer id) {
        redeterminacionRepositorio.deleteById(id);
    }

    public Double calcularVR(Double ponderadorTotal, Double valMesBase, Double valMesAnterior) {
        if (valMesBase == 0.0d) {
            return 0.0d;
        }
        return (valMesAnterior / valMesBase) * ponderadorTotal;
    }

    public List<Double> factoresRedet(Obra obra, Redeterminacion redet) {
        List<Double> factores = new ArrayList<>();
        for (Item item : obra.getItems()) {
            factores.add(valorFactorRede(item, redet.getMesSolicitud(), redet.getMesSolictudAnterior()));
        }
        return factores;
    }

    private Double valorFactorRede(Item item, LocalDate mesSolicitud, LocalDate mesAnterior) {
        Double factorRedet = 0.0d;
        for (IncidenciaFactor inFac : item.getIncidenciaFactores()) {
            Double indiceNuevo = iopServ.getValorPorMes(mesSolicitud, inFac.getIndice());
            Double indiceBase = iopServ.getValorPorMes(mesAnterior, inFac.getIndice());
            factorRedet += calcularVR(inFac.getPorcentajeIncidencia(), indiceNuevo, indiceBase);
        }
        return factorRedet;
    }
}
