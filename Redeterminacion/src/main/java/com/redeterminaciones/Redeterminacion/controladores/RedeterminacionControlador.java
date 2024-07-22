package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.IOP;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.servicios.IOPServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import com.redeterminaciones.Redeterminacion.servicios.RedeterminacionServicio;
import com.redeterminaciones.Redeterminacion.servicios.ValorMesServicio;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
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

    @GetMapping
    public String calcularRedeterminacion(@PathVariable String nombre, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate mesSolicitud) {
        Double polinomica = 0d;
        Obra obra = obraServicio.buscarPorNombre(nombre);
        redetServicio.crearRedeterminacion(mesSolicitud, obra.getFechaDeContrato());
        for (IOP indice : iopServicio.todosLosIndices()) {
            int orden = indice.getId();
            polinomica += redetServicio.calcularVR(iopServicio.ponderadorTotal(orden, obra.getItems()), polinomica, polinomica);
        }
        return "algo";
    }
}
