package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.IOP;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.Redeterminacion;
import com.redeterminaciones.Redeterminacion.servicios.IOPServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import com.redeterminaciones.Redeterminacion.servicios.RedeterminacionServicio;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
        map.addAttribute("nombreObra", obra.getNombre());
        return "redeterminacion.html";
    }

    @GetMapping("/resumen/{nombre}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> reporteRedetermincaion(@PathVariable String nombre, ModelMap map) {
        Obra obra = obraServicio.buscarPorNombre(nombre);
        Redeterminacion redet = obraServicio.buscarUltimaRedeterminacion(obra.getId());
        List<Double> factorRedet = redetServicio.factoresRedet(obra, redet);
        List<Double> remaTeorico = redetServicio.listaRemanenteTeorico(obra.getItems(), redet.getMesSolicitud());
        List<Double> remaReal = redetServicio.listaRemanenteReal(obra.getItems(), redet.getMesSolicitud());
        List<Double> minimo = redetServicio.menorRemanentes(factorRedet, factorRedet);
        Map<String, Object> data = new HashMap<>();
        data.put("items", obra.getItems());
        data.put("factoresRedet", factorRedet);
        data.put("nuevosUnitarios", redetServicio.listaPreciosUnitariosNuevos(obra.getItems(), factorRedet));
        data.put("remanenteTeorico", remaTeorico);
        data.put("remanenteReal", remaReal);
        data.put("minimo", minimo);
        data.put("incrementosSubtotal", redetServicio.listaIncrementosSubTotal(obra.getItems(), factorRedet, minimo));
        return ResponseEntity.ok(data);
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
//        map.addAttribute("minimo", minimo);
//        map.addAttribute("items", obra.getItems());
//        map.addAttribute("nuevosUnitarios", redetServicio.listaPreciosUnitariosNuevos(obra.getItems(), factorRedet));
//        map.addAttribute("remanenteReal", remaReal);
//        map.addAttribute("remanenteTeorico", remaTeorico);
//        map.addAttribute("incrementosSubtotal", redetServicio.listaIncrementosSubTotal(obra.getItems(), factorRedet, minimo));
//        map.addAttribute("factoresRedet", factorRedet);
//        map.addAttribute("valorReferencia", valorReferencia);