package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.ClienteEmpresa;
import com.redeterminaciones.Redeterminacion.entidades.ComputoYPresupuesto;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.enumeraciones.Rubros;
import com.redeterminaciones.Redeterminacion.enumeraciones.TipoDeRedeterminaciones;
import com.redeterminaciones.Redeterminacion.servicios.ClienteEmpresaServicio;
import com.redeterminaciones.Redeterminacion.servicios.ComputoYPresupuestoServicio;
import com.redeterminaciones.Redeterminacion.servicios.ExelServicio;
import com.redeterminaciones.Redeterminacion.servicios.ItemServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
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
    private ComputoYPresupuestoServicio computoYPresupuestoServicio;
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
    public String obraGuardar(@RequestParam String nombre, @RequestParam Double total,
            @RequestParam String fechaDeContrato, @RequestParam Double porcentajeDeAnticipo,
            @RequestParam String fechaDeFinalizacion, TipoDeRedeterminaciones tipoDeRedeterminaciones,
            HttpSession session, ModelMap map) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha1 = formato.parse(fechaDeContrato);
        Date fecha2 = formato.parse(fechaDeFinalizacion);
//        List<Item> items = new ArrayList<>();
//        ComputoYPresupuesto cyp = computoYPresupuestoServicio.crearComputoYPresupuesto(Rubros.HOLA, items);
        Obra obra = obraServicio.crearObra(nombre, total, fecha1, fecha2, fecha2,
                porcentajeDeAnticipo, 1, fecha2, tipoDeRedeterminaciones);
        ClienteEmpresa clienteEmpresa = (ClienteEmpresa) session.getAttribute("usuariosession");
        clienteEmpresaServicio.guardarObra(obra, clienteEmpresa.getEmail());
        return "index.html";
    }

    @GetMapping("/listaItems/{nombre}")
    public String listasDeItems(@PathVariable String nombre, ModelMap map) {
        map.put("obra", obraServicio.buscarPorNombre(nombre));
        System.out.println("eeeeeeeeeeee" + nombre);
        ComputoYPresupuesto cyp = obraServicio.buscarPorNombre(nombre).getComputoYPresupuesto();
        if (cyp != null) {
            map.addAttribute("items", cyp.getItems());
        }
        return "listaDeItems.html";
    }

    @GetMapping("/exportItem/{nombre}")
    public ResponseEntity<InputStreamResource> elExportador(@PathVariable String nombre) throws Exception {
        ByteArrayInputStream stream = exelServicio.elExportador(nombre);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Items.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }

    @PostMapping("/importItem/{nombre}")
    public String importar(@PathVariable String nombre, @RequestParam("fileExcel") MultipartFile fileExcel, HttpSession session) {
        ClienteEmpresa clienteEmpresa = (ClienteEmpresa) session.getAttribute("usuariosession");
        try {
            List<Item> items = exelServicio.elImportador(fileExcel.getInputStream(), clienteEmpresa);
            if (items != null && items.size() != 0) {
                computoYPresupuestoServicio.crearComputoYPresupuesto(Rubros.HOLA, obraServicio.buscarPorNombre(nombre).getId());
                computoYPresupuestoServicio.agregarItem(items, nombre);
            }
            return "redirect:/obra/listaItems/{nombre}";
        } catch (Exception e) {
            return "redirect:/listaItems/{nombre}";
        }
    }

    @PostMapping("/cyp")
    public String calculoCYP(HttpSession session, ModelMap map) {
//        cyps.crearComputoYPresupuesto(Rubros.HOLA, items);
        return "";
    }

    @GetMapping("/registrar/item/{nombre}")
    public String registrarItem(@PathVariable String nombre, ModelMap map) {
        map.put("obra", obraServicio.buscarPorNombre(nombre));
        return "formItem.html";
    }
}
