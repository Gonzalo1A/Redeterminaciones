package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.ClienteEmpresa;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.enumeraciones.TipoDeRedeterminaciones;
import com.redeterminaciones.Redeterminacion.servicios.ClienteEmpresaServicio;
import com.redeterminaciones.Redeterminacion.servicios.ExelServicio;
import com.redeterminaciones.Redeterminacion.servicios.ItemServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
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
        Obra obra = obraServicio.crearObra(nombre, fechaPresentacionObra, fechaDeContrato, fechaDeReeplanteo,
                porcentajeDeAnticipo, diasPlazoDeObra, tipoDeRedeterminaciones, comitente);
        ClienteEmpresa clienteEmpresa = (ClienteEmpresa) session.getAttribute("usuariosession");
        clienteEmpresaServicio.guardarObra(obra, clienteEmpresa.getEmail());
        return "index.html";
    }

    @GetMapping("/computo&presupuesto/{nombre}")
    public String calculoCYP(@PathVariable String nombre, HttpSession session, ModelMap map) {
        Obra obra = obraServicio.buscarPorNombre(nombre);
        obraServicio.calcularTotal(nombre);
        map.addAttribute("items", obra.getItems());
        map.addAttribute("total", obra.getTotal());
        return "obraView.html";
    }

    @GetMapping("/exportEstructuraDeCosto/{nombre}")
    public ResponseEntity<InputStreamResource> costosExcel(@PathVariable String nombre) throws Exception {
        ByteArrayInputStream stream = exelServicio.excelDeEstructutaDeCosto(nombre);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + nombre + " Estructura de costo.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }

    @GetMapping("/exportModeloDeAvanceDeObraTeorico/{nombre}")
    public ResponseEntity<InputStreamResource> modeloDeAvanceTeorico(@PathVariable String nombre) throws Exception {
        ByteArrayInputStream stream = itemServicio.exportarModeloParaAvanceDeObraTeoricoExcel(obraServicio.buscarPorNombre(nombre));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + nombre + " Avance de Obra Teorico.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }

    @PostMapping("/importAvanceTeoricoPorExcel")
    public String importarAvanceTeoricoExcel(@RequestParam("fileExcel") MultipartFile fileExcel) throws IOException, Exception {
        itemServicio.importarAvanceDeObraTeoricoPorExcel(fileExcel.getInputStream());
        return "redirect:/obra";
    }

    @GetMapping("/estructura_costo/{nombre}")
    public String estructura(@PathVariable String nombre, ModelMap map) {
        Obra obra = obraServicio.buscarPorNombre(nombre);
        List<String> valInc = itemServicio.cadenaIncidencias(obra.getItems());
        map.addAttribute("obra", obra);
        map.addAttribute("cadenasCargadas", valInc);
        if (obra.getItems() != null) {
            map.addAttribute("items", obra.getItems());
        }
        return "doc_estructura.html";
    }
}
