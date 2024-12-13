package com.redet.redeterminacion.servicios;


import com.redet.redeterminacion.entidades.Item;
import com.redet.redeterminacion.entidades.Obra;
import com.redet.redeterminacion.entidades.Redeterminacion;
import com.redet.redeterminacion.enumeraciones.TipoDeRedeterminaciones;
import com.redet.redeterminacion.repositorios.ObraRepositorio;
import static com.redet.redeterminacion.utilidades.FechaUtilidades.convertirStringALocalDate;
import com.redet.redeterminacion.utilidades.FormatearDecimal;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

@Service
public class ObraServicio {

    @Autowired
    private ObraRepositorio obraRepositorio;

    @Transactional
    public Obra crearObra(String nombre, String fechaPresentacionObra, String fechaDeContrato,
            String fechaDeReeplanteo, Double porcentajeDeAnticipo, int diasPlazoDeObra,
            TipoDeRedeterminaciones tipoDeRedet, String comitente) {
        Obra nuevaObra = new Obra();
        nuevaObra.setNombre(nombre);
        nuevaObra.setFechaPresentacionObra(convertirStringALocalDate(fechaPresentacionObra));
        nuevaObra.setFechaDeContrato(convertirStringALocalDate(fechaDeContrato));
        nuevaObra.setFechaDeReeplanteo(convertirStringALocalDate(fechaDeReeplanteo));
        nuevaObra.setFechaDeFinalizacion(calcularFecha(convertirStringALocalDate(fechaDeReeplanteo), diasPlazoDeObra));
        nuevaObra.setPorcentajeDeAnticipo(FormatearDecimal.porcentajes(porcentajeDeAnticipo));
        nuevaObra.setDiasPlazoDeObra(diasPlazoDeObra);
        nuevaObra.setTipoDeRedet(tipoDeRedet);
        nuevaObra.setComitente(comitente);
        obraRepositorio.save(nuevaObra);
        return nuevaObra;
    }

    @Transactional
    public void modificarObra(String idObra, String nuevoNombre,
            Double total, LocalDate fechaPresentacionObra, LocalDate fechaDeContrato,
            LocalDate fechaDeReeplanteo, Double porcentajeDeAnticipo, int diasPazoDeObra,
            LocalDate fechaDeFinalizacion, TipoDeRedeterminaciones tipoDeRedet,
            List<Item> items) {
        Optional<Obra> respuesta = obraRepositorio.findById(idObra);
        if (respuesta != null) {
            Obra obra = respuesta.get();
            obra.setNombre(nuevoNombre);
            obra.setTotal(total);
            obra.setFechaPresentacionObra(fechaPresentacionObra);
            obra.setFechaDeContrato(fechaDeContrato);
            obra.setFechaDeReeplanteo(fechaDeReeplanteo);
            obra.setPorcentajeDeAnticipo(FormatearDecimal.porcentajes(porcentajeDeAnticipo));
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
    public void agregarRedeterminacion(Redeterminacion redet, String nombreObra) {
        Obra obra = buscarPorNombre(nombreObra);
        if (obra != null) {
            if (obra.getRedeterminaciones() == null) {
                obra.setRedeterminaciones(new ArrayList<>());
            }
            obra.getRedeterminaciones().add(redet);
        } else {
            throw new EntityNotFoundException("Obra con nombre " + nombreObra + " no encontrada");
        }
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

        obra.setTotal(FormatearDecimal.cuatroDecimales(total));
        obraRepositorio.save(obra);
        return total;
    }

//    private Date calcularFecha(Date fechaReplanteo, int dias) {
//        long a = fechaReplanteo.getTime() + dias * 86400000;
//        return new Date(a);
//    }
    private LocalDate calcularFecha(LocalDate fechaReplanteo, int dias) {
        return fechaReplanteo.plusDays(dias);
    }

    public Obra buscarPorNombre(String nombre) {
        return obraRepositorio.buscarObraPorNombre(nombre);
    }

    public @DateTimeFormat(pattern = "yyyy-MM")
    LocalDate buscarMesSolicitudAnterior(String ObraId) {
        return obraRepositorio.buscarMesUltimaSolicitud(ObraId);
    }
    
    public Redeterminacion buscarUltimaRedeterminacion(String ObraId) {
        return obraRepositorio.buscarUltimaRedet(ObraId);
    }

    @Transactional
    public void eliminarObra(String nombre) {
        Obra obraAEliminar = buscarPorNombre(nombre);
        obraRepositorio.delete(obraAEliminar);
    }

}
