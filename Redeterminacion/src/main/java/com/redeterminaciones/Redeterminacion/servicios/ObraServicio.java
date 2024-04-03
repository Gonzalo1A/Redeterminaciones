package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.ComputoYPresupuesto;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.enumeraciones.TipoDeRedeterminaciones;
import com.redeterminaciones.Redeterminacion.repositorios.ComputoYPresupuestoRepositorio;
import com.redeterminaciones.Redeterminacion.repositorios.ObraRepositorio;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObraServicio {

    @Autowired
    private ObraRepositorio obraRepositorio;
    @Autowired
    private ComputoYPresupuestoRepositorio cyprepo;

    @Transactional
    public Obra crearObra(String nombre, Double total, Date fechaPresentacionObra, Date fechaDeContrato,
            Date fechaDeReeplanteo, Double porcentajeDeAnticipo, int diasPlazoDeObra,
            TipoDeRedeterminaciones tipoDeRedet) {
        Obra nuevaObra = new Obra();
        nuevaObra.setNombre(nombre);
        nuevaObra.setTotal(total);
        nuevaObra.setFechaPresentacionObra(fechaPresentacionObra);
        nuevaObra.setFechaDeContrato(fechaDeContrato);
        nuevaObra.setFechaDeReeplanteo(fechaDeReeplanteo);
        nuevaObra.setPorcentajeDeAnticipo(porcentajeDeAnticipo);
        nuevaObra.setDiasPlazoDeObra(diasPlazoDeObra);
        nuevaObra.setTipoDeRedet(tipoDeRedet);
        obraRepositorio.save(nuevaObra);
        return nuevaObra;
    }

    @Transactional
    public void modificarObra(String idObra, String nuevoNombre,
            Double total, Date fechaPresentacionObra, Date fechaDeContrato,
            Date fechaDeReeplanteo, Double porcentajeDeAnticipo, int diasPazoDeObra,
            Date fechaDeFinalizacion, TipoDeRedeterminaciones tipoDeRedet,
            ComputoYPresupuesto computoYPresupuesto) {
        Obra obraAModificar = obraRepositorio.getById(idObra);
        if (obraAModificar != null) {
            obraAModificar.setNombre(nuevoNombre);
            obraAModificar.setTotal(total);
            obraAModificar.setFechaPresentacionObra(fechaPresentacionObra);
            obraAModificar.setFechaDeContrato(fechaDeContrato);
            obraAModificar.setFechaDeReeplanteo(fechaDeReeplanteo);
            obraAModificar.setPorcentajeDeAnticipo(porcentajeDeAnticipo);
            obraAModificar.setDiasPlazoDeObra(diasPazoDeObra);
            obraAModificar.setFechaDeFinalizacion(fechaDeFinalizacion);
            obraAModificar.setTipoDeRedet(tipoDeRedet);
            obraAModificar.setComputoYPresupuesto(computoYPresupuesto);
            obraRepositorio.save(obraAModificar);
        }
    }

    @Transactional
    public void agregarCyP(String idCyp, String idObra) {
        Optional<Obra> rOptional = obraRepositorio.findById(idObra);
        Optional<ComputoYPresupuesto> rOptional2 = cyprepo.findById(idCyp);
        ComputoYPresupuesto cyp = new ComputoYPresupuesto();
        Obra obra = new Obra();
        if (rOptional.isPresent()) {
            obra = rOptional.get();
        }

        if (rOptional.isPresent()) {
            cyp = rOptional2.get();
        }
        obra.setComputoYPresupuesto(cyp);
        obraRepositorio.save(obra);
    }

    public Obra buscarPorNombre(String nombre) {
        return obraRepositorio.buscarObraPorNombre(nombre);
    }

    @Transactional
    public void eliminarObra(String nombre) {
        Obra obraAEliminar = buscarPorNombre(nombre);
        obraRepositorio.delete(obraAEliminar);
    }
}
