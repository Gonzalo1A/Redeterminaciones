package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.IOP;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.Redeterminacion;
import com.redeterminaciones.Redeterminacion.servicios.IOPServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import com.redeterminaciones.Redeterminacion.servicios.RedeterminacionServicio;
import java.time.LocalDate;
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

    @GetMapping("/valor_referencia/{nombre}")
    public String calcularRedeterminacion(@PathVariable String nombre,
            ModelMap map) {
        Double polinomica = 0d;
        LocalDate mesSolicitud = LocalDate.now();
        Obra obra = obraServicio.buscarPorNombre(nombre);
        Redeterminacion redet = crearRedeterminacion(mesSolicitud.plusMonths(2), obra);
        obraServicio.agregarRedeterminacion(redet, nombre);
        for (IOP indice : iopServicio.todosLosIndices()) {
            int orden = indice.getId();
            Double indiceNuevo = valorMes(mesSolicitud.plusMonths(2), orden);
            Double indiceBase = valorMes(redet.getMesSolictudAnterior(), orden);
            polinomica += redetServicio.calcularVR(iopServicio.ponderadorTotal(orden, obra.getItems()), indiceNuevo, indiceBase);
        }
        redetServicio.modificar(polinomica, redet.getIdRedet());
        map.addAttribute("valorReferencia", polinomica*100);
        return "redeterminacion.html";
    }

    private Redeterminacion crearRedeterminacion(LocalDate mesSol, Obra obra) {
        LocalDate mesSolAnt = obraServicio.buscarMesSolicitudAnterior(obra.getId());
        if (mesSolAnt != null) {
            return redetServicio.crearRedeterminacion(mesSol, mesSolAnt);
        } else {
            return redetServicio.crearRedeterminacion(mesSol, obra.getFechaDeContrato());
        }
    }

    private Double valorMes(LocalDate mes, Integer idIOP) {
        LocalDate mesAnterior = mes.minusMonths(1);
        return iopServicio.getValorPorMes(mesAnterior, idIOP);
    }
}
