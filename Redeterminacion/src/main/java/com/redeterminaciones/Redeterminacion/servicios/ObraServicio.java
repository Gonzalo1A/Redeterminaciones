package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.enumeraciones.TipoDeRedeterminaciones;
import com.redeterminaciones.Redeterminacion.repositorios.ObraRepositorio;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObraServicio {

    @Autowired
    private ObraRepositorio obraRepositorio;

    @Transactional
    public Obra crearObra(String nombre, Date fechaPresentacionObra, Date fechaDeContrato,
            Date fechaDeReeplanteo, Double porcentajeDeAnticipo, int diasPlazoDeObra,
            TipoDeRedeterminaciones tipoDeRedet, String comitente) {
        Obra nuevaObra = new Obra();
        nuevaObra.setNombre(nombre);
        nuevaObra.setFechaPresentacionObra(fechaPresentacionObra);
        nuevaObra.setFechaDeContrato(fechaDeContrato);
        nuevaObra.setFechaDeReeplanteo(fechaDeReeplanteo);
        nuevaObra.setFechaDeFinalizacion(calularFecha(fechaDeReeplanteo, diasPlazoDeObra));
        nuevaObra.setPorcentajeDeAnticipo(porcentajeDeAnticipo);
        nuevaObra.setDiasPlazoDeObra(diasPlazoDeObra);
        nuevaObra.setTipoDeRedet(tipoDeRedet);
        nuevaObra.setComitente(comitente);
        obraRepositorio.save(nuevaObra);
        return nuevaObra;
    }

    @Transactional
    public void modificarObra(String idObra, String nuevoNombre,
            String total, Date fechaPresentacionObra, Date fechaDeContrato,
            Date fechaDeReeplanteo, Double porcentajeDeAnticipo, int diasPazoDeObra,
            Date fechaDeFinalizacion, TipoDeRedeterminaciones tipoDeRedet,
            List<Item> items) {
        Optional<Obra> respuesta = obraRepositorio.findById(idObra);
        if (respuesta != null) {
            Obra obra = respuesta.get();
            obra.setNombre(nuevoNombre);
            obra.setTotal(total);
            obra.setFechaPresentacionObra(fechaPresentacionObra);
            obra.setFechaDeContrato(fechaDeContrato);
            obra.setFechaDeReeplanteo(fechaDeReeplanteo);
            obra.setPorcentajeDeAnticipo(porcentajeDeAnticipo);
            obra.setDiasPlazoDeObra(diasPazoDeObra);
            obra.setFechaDeFinalizacion(fechaDeFinalizacion);
            obra.setTipoDeRedet(tipoDeRedet);
            obra.setItems(items);
            obraRepositorio.save(obra);
        }
    }

    @Transactional
    public void agregarItem(List<Item> items, String nombreObra) {
        Obra obra = buscarPorNombre(nombreObra);
        obra.setItems(items);
        obraRepositorio.save(obra);
    }

    @Transactional
    public Double calcularTotal(String nombreObra) {
        Obra obra = buscarPorNombre(nombreObra);
        double total = 0d;
        for (Item item : obra.getItems()) {
            if (item.getSubTotal() != null) {
                total += item.getSubTotal();
            }
        }
        BigDecimal resultado = new BigDecimal(total);
        obra.setTotal(resultado.setScale(4, RoundingMode.HALF_UP).toString());
        obraRepositorio.save(obra);
        return total;
    }

    private Date calularFecha(Date fechaReplanteo, int dias) {
        long a = fechaReplanteo.getTime() + dias * 86400000;
        return new Date(a);
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
