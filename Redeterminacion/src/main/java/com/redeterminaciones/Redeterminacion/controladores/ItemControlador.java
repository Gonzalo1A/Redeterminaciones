package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.AvanceObraReal;
import com.redeterminaciones.Redeterminacion.utilidades.DatosRecibidos;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.ValorMes;
import com.redeterminaciones.Redeterminacion.utilidades.ConjuntoIdValorFecha;
import com.redeterminaciones.Redeterminacion.servicios.AvanceRealServicio;
import com.redeterminaciones.Redeterminacion.servicios.IncidenciaFactorServicio;
import com.redeterminaciones.Redeterminacion.servicios.ItemServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import com.redeterminaciones.Redeterminacion.servicios.ValorMesServicio;
import com.redeterminaciones.Redeterminacion.utilidades.DatosAvanceObra;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    private AvanceRealServicio avanceRealServicio;
    @Autowired
    private ValorMesServicio valorMesServicio;

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
        List<ConjuntoIdValorFecha> listaIncidencias = datos.getListaDatos();
        for (ConjuntoIdValorFecha incidencia : listaIncidencias) {
            if (incidencia.getValor() != null) {
                itemServicio.agregarFactor(Long.valueOf(incidencia.getItemId()), incidenciaFactorServicio.formatearValores(incidencia.getValor()));
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

    @GetMapping("/avance_obra/{nombre}")
    public String caragarAvanceDeObraReal(@PathVariable String nombre, ModelMap model) {
        Obra obra = obraServicio.buscarPorNombre(nombre);
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("MM/yyyy");
        if (obra.getItems() != null) {
            model.addAttribute("items", obra.getItems());
        }
        model.addAttribute("fecha", fechaActual.format(formato));
        return "form_avanceReal.html";
    }

    @PostMapping("/avance_carga")
    public String cargaAvanceObra(@RequestBody DatosAvanceObra datos) throws ParseException {
        List<ConjuntoIdValorFecha> conjuntoIdValorFechas = datos.getValorMes();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy");
        for (ConjuntoIdValorFecha valor : conjuntoIdValorFechas) {
            if (valor.getValor() != null) {
                Long idItem = Long.valueOf(valor.getItemId());
                Item item = itemServicio.getOne(idItem);
                ValorMes valMes = valorMesServicio.crear(formatter.parse(valor.getFecha()),Double.valueOf(valor.getValor()));
                List<AvanceObraReal> lista = avanceRealServicio.cargarAvance(item, valMes);
                itemServicio.agregarAvanceReal(idItem,lista);
            }
        }
        return "form_avanceReal.html";
    }

}
