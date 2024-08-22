package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.IOP;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.Redeterminacion;
import com.redeterminaciones.Redeterminacion.servicios.IOPServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import com.redeterminaciones.Redeterminacion.servicios.RedeterminacionServicio;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
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

        for (IOP indice : iopServicio.todosLosIndices()) {
            int orden = indice.getId();
            Double indiceNuevo = iopServicio.getValorPorMes(redet.getMesSolicitud(), orden);
            Double indiceBase = iopServicio.getValorPorMes(redet.getMesSolictudAnterior(), orden);
            polinomica += redetServicio.calcularVR(iopServicio.ponderadorTotal(orden, obra.getItems()), indiceNuevo, indiceBase);
        }
        redetServicio.modificar(polinomica, redet.getIdRedet());
        map.addAttribute("valorReferencia", polinomica * 100);
        return "redeterminacion.html";
    }

    @GetMapping("")
    public String reporteRedetermincaion(@PathVariable String nombre, @RequestParam Integer idRedet, ModelMap map) {
        Obra obra = obraServicio.buscarPorNombre(nombre);
        Redeterminacion redet = redetServicio.buscarRedeterminacion(idRedet);
        map.addAttribute("factoresRedet", redetServicio.factoresRedet(obra, redet));
        return "redeterminacion.html";
    }

    private Redeterminacion crearRedeterminacion(LocalDate mesSol, Obra obra) {
        LocalDate mesSolAnt = obraServicio.buscarMesSolicitudAnterior(obra.getId());
        Redeterminacion res;
        if (mesSolAnt != null) {
            if (mesSolAnt.getYear() == mesSol.getYear() && mesSolAnt.getMonth() == mesSol.getMonth()) {
                return obraServicio.buscarUltimaRedeterminacion(obra.getId());
            } else {
                res = redetServicio.crearRedeterminacion(mesSol, mesSolAnt);
                obraServicio.agregarRedeterminacion(res, obra.getNombre());
                return res;
            }
        } else {
            res = redetServicio.crearRedeterminacion(mesSol, obra.getFechaDeContrato());
            obraServicio.agregarRedeterminacion(res, obra.getNombre());
            return res;
        }
    }

}