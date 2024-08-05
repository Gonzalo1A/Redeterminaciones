package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.IOP;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.Redeterminacion;
import com.redeterminaciones.Redeterminacion.servicios.IOPServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import com.redeterminaciones.Redeterminacion.servicios.RedeterminacionServicio;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/calculo")
public class RedeterminacionControlador {

    @Autowired
    private RedeterminacionServicio redetServicio;
    @Autowired
    private ObraServicio obraServicio;
    @Autowired
    private IOPServicio iopServicio;

    @GetMapping("/valor_referencia")
    public String calcularRedeterminacion(@PathVariable String nombre,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") LocalDate mesSolicitud,
            ModelMap map) {
        Double polinomica = 0d;
        Obra obra = obraServicio.buscarPorNombre(nombre);
        crearRedterminacion(mesSolicitud, obra);
        for (IOP indice : iopServicio.todosLosIndices()) {
            int orden = indice.getId();
            Double indiceNuevo = valorMes(mesSolicitud, orden);
            Double indiceBase = valorMes(obraServicio.buscarMesSolicitudAnterior(nombre), orden);
            polinomica += redetServicio.calcularVR(iopServicio.ponderadorTotal(orden, obra.getItems()), indiceNuevo, indiceBase);
        }
        map.addAttribute("valorReferencia", polinomica);
        return "redeterminacion.html";
    }

    private Redeterminacion crearRedterminacion(LocalDate mesSol, Obra obra) {
        if (obra.getRedeterminaciones() != null) {
            return redetServicio.crearRedeterminacion(mesSol, obraServicio.buscarMesSolicitudAnterior(obra.getNombre()));
        } else {
            return redetServicio.crearRedeterminacion(mesSol, obra.getFechaDeContrato());
        }
    }

    private Double valorMes(LocalDate mes, Integer idIOP) {
        LocalDate mesAnterior = mes.minusMonths(1);
        return iopServicio.getValorPorMes(mesAnterior, idIOP);
    }
}
