package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.DatosRecibidos;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.ValoresIncidenciaLista;
import com.redeterminaciones.Redeterminacion.servicios.IncidenciaFactorServicio;
import com.redeterminaciones.Redeterminacion.servicios.ItemServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/item")
public class ItemControlador {

    @Autowired
    private ItemServicio itemServicio;
    @Autowired
    private ObraServicio obraServicio;
    @Autowired
    private IncidenciaFactorServicio incidenciaFactorServicio;

    @GetMapping("/listaItems/{nombre}")
    public String listasDeItems(@PathVariable String nombre, ModelMap map) {
        Obra obra = obraServicio.buscarPorNombre(nombre);
        List<String> valInc = itemServicio.cadenaIncidencias(obra.getItems());
        map.addAttribute("obra", obra);
        map.addAttribute("cadenasCargadas", valInc);
        if (obra.getItems() != null) {
            map.addAttribute("items", obra.getItems());
        }
        return "listaDeItems.html";
    }
    @PostMapping("/cargarIncidencia")
    public String cargarIncidenciaFactor(@RequestBody DatosRecibidos datos) {
        String nombreObra = datos.getNombreObra();
        List<ValoresIncidenciaLista> listaIncidencias = datos.getListaDatos();
        for (ValoresIncidenciaLista incidencia : listaIncidencias) {
            if (incidencia.getIncidencia() != null) {
                itemServicio.agregarFactor(incidencia.getItemId(), incidenciaFactorServicio.formatearValores(incidencia.getIncidencia()));
            }
        }
        return "redirect:/obra/listaItems/" + nombreObra;
    }
}
