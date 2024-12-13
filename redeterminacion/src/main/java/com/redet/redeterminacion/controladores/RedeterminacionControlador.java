package com.redet.redeterminacion.controladores;

import com.redet.redeterminacion.entidades.IOP;
import com.redet.redeterminacion.entidades.Obra;
import com.redet.redeterminacion.entidades.Redeterminacion;
import com.redet.redeterminacion.servicios.IOPServicio;
import com.redet.redeterminacion.servicios.ObraServicio;
import com.redet.redeterminacion.servicios.RedeterminacionServicio;
import com.redet.redeterminacion.utilidades.FormatearDecimal;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
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
//        LocalDate mesSolicitud = LocalDate.now();
        Obra obra = obraServicio.buscarPorNombre(nombre);
        Redeterminacion redet = crearRedeterminacion(LocalDate.now(), obra);

        for (IOP indice : iopServicio.todosLosIndices()) {
            int orden = indice.getId();
            Double indiceNuevo = iopServicio.getValorPorMes(redet.getMesSolicitud(), orden);
            Double indiceBase = iopServicio.getValorPorMes(redet.getMesSolictudAnterior(), orden);
            polinomica += redetServicio.calcularVR(iopServicio.ponderadorTotal(orden, obra.getItems()), indiceBase, indiceNuevo);
        }
        /* 
        crear condicional que elimine la redeterminacion de la base 
        si la polinomica da valor de referencia (polinomica) menor al 110%
         */

        redetServicio.modificar(polinomica, redet.getIdRedet());
        map.addAttribute("valorReferencia", FormatearDecimal.dosDecimales(polinomica));
        map.addAttribute("nombreObra", obra.getNombre());
        map.addAttribute("idRedet", redet.getIdRedet());
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
        List<Double> minimo = redetServicio.menorRemanentes(remaTeorico, remaReal);
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

    @GetMapping("/descargar_resumen/{nombre}")
    public ResponseEntity<InputStreamResource> exportarItems(@PathVariable String nombre, @RequestParam Integer idRedet) {
        Redeterminacion redet = redetServicio.buscarRedeterminacion(idRedet);
        ByteArrayInputStream stream = null;
        try {
            stream = redetServicio.exportarRepoteDeRedeterminacion(obraServicio.buscarPorNombre(nombre), redet.getMesSolicitud(), redet.getMesSolictudAnterior());
            redetServicio.guardarResumen(redetServicio.convertInputStreamToByteArray(stream), idRedet);
        } catch (Exception ex) {
            Logger.getLogger(RedeterminacionControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + nombre + " Resumen.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
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
