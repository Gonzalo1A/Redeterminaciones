package com.redet.redeterminacion.controladores;

import com.redet.redeterminacion.entidades.AvanceObraReal;
import com.redet.redeterminacion.entidades.IncidenciaFactor;
import com.redet.redeterminacion.utilidades.DatosRecibidos;
import com.redet.redeterminacion.entidades.Item;
import com.redet.redeterminacion.entidades.Obra;
import com.redet.redeterminacion.entidades.ValorMes;
import com.redet.redeterminacion.utilidades.ConjuntoIdValorFecha;
import com.redet.redeterminacion.servicios.AvanceRealServicio;
import com.redet.redeterminacion.servicios.IncidenciaFactorServicio;
import com.redet.redeterminacion.servicios.ItemServicio;
import com.redet.redeterminacion.servicios.ObraServicio;
import com.redet.redeterminacion.servicios.ValorMesServicio;
import com.redet.redeterminacion.utilidades.DatosAvanceObra;
import com.redet.redeterminacion.utilidades.FechaUtilidades;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
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
            System.out.println(incidencia.getValor());
            if (incidencia.getValor() != null) {
                List<IncidenciaFactor> listaIncFac = incidenciaFactorServicio.formatearValores(incidencia.getValor());
                itemServicio.agregarFactor(Long.valueOf(incidencia.getItemId()), listaIncFac);
            }
        }
        return "redirect:/item/listaItems/" + nombreObra;
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
            return "redirect:/item/lista/{nombre}";
        } catch (Exception e) {
            return "redirect:/item/lista/{nombre}";
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
        int cantMeses = FechaUtilidades.cantidadMeses(obra.getFechaDeReeplanteo(), LocalDate.now());
        List<LocalDate> listaMeses = FechaUtilidades.generarListaFechas(obra.getFechaDeReeplanteo(), cantMeses);
        model.addAttribute("obra", obra);
        if (obra.getItems() != null) {
            model.addAttribute("items", obra.getItems());
        }
        model.addAttribute("fechas", listaMeses);
        return "form_avanceReal.html";
    }


    @PostMapping("/avance_carga")
    public String cargaAvanceObra(@RequestBody DatosAvanceObra datos) throws ParseException {
        List<ConjuntoIdValorFecha> conjuntoIdValorFechas = datos.getValorMes();
//        SimpleDateFormat formatter = new SimpleDateFormat("MM/yyyy");        DateTimeFormatter formato = DateTimeFormatter.ofPattern("MM/yyyy");
//        LocalDate fechaActual = LocalDate.of(2024, 7, 1);formatter.parse(fechaActual.format(formato))
        for (ConjuntoIdValorFecha valor : conjuntoIdValorFechas) {
            if (valor.getValor() != null) {
                Long idItem = Long.valueOf(valor.getItemId());
                Item item = itemServicio.getOne(idItem);
                LocalDate fechaActual = FechaUtilidades.convertirStringALocalDateFormato2(valor.getFecha());
                ValorMes valMes = valorMesServicio.crear(fechaActual, Double.valueOf(valor.getValor()));
                List<AvanceObraReal> lista = avanceRealServicio.cargarAvance(item, valMes);
                itemServicio.agregarAvanceReal(idItem, lista);
            }
        }
        return "form_avanceReal.html";
    }

    @PostMapping("/avanceObraRealExport")
    public ResponseEntity<InputStreamResource> caragarAvanceDeObraRealPorExcel(@RequestParam String nombre, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) throws Exception {
        Obra obra = obraServicio.buscarPorNombre(nombre);
        ByteArrayInputStream stream = avanceRealServicio.exportarModeloDeCargaDeAvanceRealExcel(obra, fecha);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + nombre + " Avance Real.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }

    @PostMapping("/importAvanceRealExcel")
    public String importarAvanceRealPorExcel(@RequestParam("fileExcel") MultipartFile fileExcel) throws IOException, Exception {
        itemServicio.importarAvnaceRealMensualPorExel(fileExcel.getInputStream());
        return "form_avanceReal.html";
    }
}
