package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.ClienteEmpresa;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.enumeraciones.TipoDeRedeterminaciones;
import com.redeterminaciones.Redeterminacion.servicios.ClienteEmpresaServicio;
import com.redeterminaciones.Redeterminacion.servicios.ExelServicio;
import com.redeterminaciones.Redeterminacion.servicios.IOPServicio;
import com.redeterminaciones.Redeterminacion.servicios.IncidenciaFactorServicio;
import com.redeterminaciones.Redeterminacion.servicios.ItemServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/obra")
public class ObraControlador {

    @Autowired
    private ObraServicio obraServicio;
    @Autowired
    private ItemServicio itemServicio;
    @Autowired
    private ClienteEmpresaServicio clienteEmpresaServicio;
    @Autowired
    private IncidenciaFactorServicio incidenciaFactorServicio;
    @Autowired
    private IOPServicio iOPServicio;
    @Autowired
    private ExelServicio exelServicio;

    @GetMapping("/")
    public String obras(@PathVariable String id) {
        return "obras.html";
    }

    @GetMapping("/registrar")
    public String registrarObra() {
        return "formObra.html";
    }

    @PostMapping("/registrarObra")
    public String obraGuardar(@RequestParam String nombre,
            @RequestParam String fechaDeContrato, @RequestParam Double porcentajeDeAnticipo,
            @RequestParam String fechaDeReeplanteo, @RequestParam int diasPlazoDeObra, TipoDeRedeterminaciones tipoDeRedeterminaciones,
            @RequestParam String fechaPresentacionObra, @RequestParam String comitente, HttpSession session, ModelMap map) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha1 = formato.parse(fechaDeContrato);
        Date fecha2 = formato.parse(fechaDeReeplanteo);
        Date fecha3 = formato.parse(fechaPresentacionObra);
        Obra obra = obraServicio.crearObra(nombre, fecha3, fecha1, fecha2,
                porcentajeDeAnticipo, diasPlazoDeObra, tipoDeRedeterminaciones, comitente);
        ClienteEmpresa clienteEmpresa = (ClienteEmpresa) session.getAttribute("usuariosession");
        clienteEmpresaServicio.guardarObra(obra, clienteEmpresa.getEmail());
        return "index.html";
    }

    @GetMapping("/listaItems/{nombre}")
    public String listasDeItems(@PathVariable String nombre, ModelMap map) {
        Obra obra = obraServicio.buscarPorNombre(nombre);
        map.put("obra", obra);
        if (obra.getItems() != null) {
            map.addAttribute("items", obra.getItems());
        }
        return "listaDeItems.html";
    }

    @PostMapping("/cargarIncidendcia")
    public String cargarIncidenciaFactor(@RequestParam Long idItem,
            @RequestParam String incidenciaFactor) {
        itemServicio.agregarFactor(idItem, incidenciaFactorServicio.formatearValores(incidenciaFactor));
        return "listaDeItems.html";
    }

    @GetMapping("/exportItem/{nombre}")
    public ResponseEntity<InputStreamResource> elExportador(@PathVariable String nombre) throws Exception {
        ByteArrayInputStream stream = exelServicio.elExportador(nombre);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + nombre + " Items.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }

    @GetMapping("/registrar/item/{nombre}")
    public String registrarItem(@PathVariable String nombre, ModelMap map) {
        map.put("obra", obraServicio.buscarPorNombre(nombre));
        return "formItem.html";
    }

    @PostMapping("/importItem/{nombre}")
    public String importar(@PathVariable String nombre, @RequestParam("fileExcel") MultipartFile fileExcel) {
        try {
            List<Item> items = exelServicio.elImportador(fileExcel.getInputStream());
            if (items != null && !items.isEmpty()) {
                obraServicio.agregarItem(items, nombre);
                obraServicio.calcularTotal(nombre);
            }
            return "redirect:/obra/listaItems/{nombre}";
        } catch (Exception e) {
            return "redirect:/obra/listaItems/{nombre}";
        }
    }

    @GetMapping("/computo&presupuesto/{nombre}")
    public String calculoCYP(@PathVariable String nombre, HttpSession session, ModelMap map) {
        Obra obra = obraServicio.buscarPorNombre(nombre);
        obraServicio.calcularTotal(nombre);
        itemServicio.calularIncidenciaItem(obra);
        map.addAttribute("items", obra.getItems());
        map.addAttribute("total", obra.getTotal());
        return "obraView.html";
    }

    @PostMapping("/importIOP")
    public String importarIOP(@RequestParam("fileExcel") MultipartFile fileExcel) throws IOException {
        exelServicio.importarIOP(fileExcel.getInputStream());
        return "redirect:/obra";
    }

    @GetMapping("/exportIOP")
    public ResponseEntity<InputStreamResource> exportIOP() throws Exception {
        ByteArrayInputStream stream = exelServicio.exportIOP();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=IOP-Cba.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }

    @GetMapping("/exportIncidenciaFactor/{nombre}")
    public ResponseEntity<InputStreamResource> incidenciaFactoresExcel(@PathVariable String nombre) throws Exception {
        ByteArrayInputStream stream = exelServicio.incidenciaFactoresExcel(nombre);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + nombre + " Factorizar.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }

    @GetMapping("/exportEstructuraDeCosto/{nombre}")
    public ResponseEntity<InputStreamResource> costosExcel(@PathVariable String nombre) throws Exception {
        ByteArrayInputStream stream = exelServicio.excelDeEstructutaDeCosto(nombre);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + nombre + " Estructura de costo.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }

    @PostMapping("/importFactoresExcel")
    public String importarFactoresExcel(@RequestParam("fileExcel") MultipartFile fileExcel) throws IOException {
        exelServicio.importarFactoresDeItemsPorExcel(fileExcel.getInputStream());
        return "redirect:/obra";
    }
}
