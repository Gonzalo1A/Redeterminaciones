package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.DatosRecibidos;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.ValoresIncidenciaLista;
import com.redeterminaciones.Redeterminacion.servicios.IncidenciaFactorServicio;
import com.redeterminaciones.Redeterminacion.servicios.ItemServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/item")
public class ItemControlador {

    @Autowired
    private ItemServicio itemServicio;
    @Autowired
    private ObraServicio obraServicio;
    @Autowired
    private IncidenciaFactorServicio incidenciaFactorServicio;

    @GetMapping("/lista/{nombre}")
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

    @GetMapping("/export/{nombre}")
    public ResponseEntity<InputStreamResource> exportarItems(@PathVariable String nombre) throws Exception {
        ByteArrayInputStream stream = itemServicio.exportarModeloParaIngresarItemsPorExcel(obraServicio.buscarPorNombre(nombre));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + nombre + " Items.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }

    @GetMapping("/registrar/{nombre}")
    public String registrarItem(@PathVariable String nombre, ModelMap map) {
        map.put("obra", obraServicio.buscarPorNombre(nombre));
        return "formItem.html";
    }

    @PostMapping("/import/{nombre}")
    public String importarItems(@PathVariable String nombre, @RequestParam("fileExcel") MultipartFile fileExcel) {
        try {
            List<Item> items = itemServicio.importarItemsPorExcel(fileExcel.getInputStream());
            if (items != null && !items.isEmpty()) {
                obraServicio.agregarItem(items, nombre);
                obraServicio.calcularTotal(nombre);
                itemServicio.calularIncidenciaItem(obraServicio.buscarPorNombre(nombre));
            }
            return "redirect:/obra/listaItems/{nombre}";
        } catch (Exception e) {
            return "redirect:/obra/listaItems/{nombre}";
        }
    }

    @GetMapping("/exportIncidenciaFactor/{nombre}")
    public ResponseEntity<InputStreamResource> incidenciaFactoresExcel(@PathVariable String nombre) throws Exception {
        ByteArrayInputStream stream = incidenciaFactorServicio.exportarLaIncidenciaDeFactoresExcel(obraServicio.buscarPorNombre(nombre));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + nombre + " Factorizar.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }

    @PostMapping("/importFactoresExcel")
    public String importarFactoresExcel(@RequestParam("fileExcel") MultipartFile fileExcel) throws IOException, Exception {
        incidenciaFactorServicio.importarFactoresDeItemsPorExcel(fileExcel.getInputStream());
        return "redirect:/obra";
    }
}
