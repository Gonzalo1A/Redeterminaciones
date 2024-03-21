package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.ClienteEmpresa;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.Usuario;

import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.enumeraciones.TipoDeRedeterminaciones;
import com.redeterminaciones.Redeterminacion.servicios.ClienteEmpresaServicio;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.redeterminaciones.Redeterminacion.servicios.EmpresaServicio;
import com.redeterminaciones.Redeterminacion.servicios.ItemServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class PortalControlador {

    @Autowired
    private ItemServicio itemServicio;
    @Autowired
    private ObraServicio obraServicio;
    @Autowired
    private EmpresaServicio empresaServicio;
    @Autowired
    private ClienteEmpresaServicio clienteEmpresaServicio;

    @GetMapping("/")
    public String index(ModelMap modelMap, HttpSession session) {
        return "index.html";
    }

    @GetMapping("/obra")
    public String listaObras(ModelMap modelMap, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        ClienteEmpresa clienteEmpresa = clienteEmpresaServicio.getCliente(usuario.getId());
        modelMap.addAttribute("obras", clienteEmpresa.getObras());
        return "obras.html";
    }

    @PostMapping("/registrarItem")
    public String itemGuardar(@RequestParam String numeroItem, @RequestParam String descripcion,
            @RequestParam String unidad, @RequestParam Double cantidad, @RequestParam Double precioUnitario) {
        itemServicio.crearItem(numeroItem, descripcion, unidad, cantidad, precioUnitario);
        return "index.html";
    }

    @GetMapping("/listaItems")
    public String listasDeItems(ModelMap map) {
        List<Item> items = itemServicio.getAll();
        map.addAttribute("items", items);
        return "listaDeItems.html";
    }

    @GetMapping("/exportItem")
    public ResponseEntity<InputStreamResource> elExportador() throws Exception {
        ByteArrayInputStream stream = itemServicio.elExportador();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=items.xls");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }

    @PostMapping("/registrarObra")
    public String obraGuardar(@RequestParam String nombre, @RequestParam Double total,
            @RequestParam String fechaDeContrato, @RequestParam Double porcentajeDeAnticipo,
            @RequestParam String fechaDeFinalizacion, TipoDeRedeterminaciones tipoDeRedeterminaciones,
            HttpSession session, ModelMap map) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha1 = formato.parse(fechaDeContrato);
        Date fecha2 = formato.parse(fechaDeFinalizacion);
        Obra obra = obraServicio.crearObra(nombre, total, fecha1, fecha2, fecha2,
                porcentajeDeAnticipo, 1, fecha2, tipoDeRedeterminaciones, null);
        ClienteEmpresa clienteEmpresa = (ClienteEmpresa) session.getAttribute("usuariosession");
        clienteEmpresaServicio.guardarObra(obra, clienteEmpresa.getEmail());
        return "index.html";
    }

    @PostMapping("/registrarEmpresa")
    public String empresaGuardar(@RequestParam String nombre, @RequestParam Double oferta) {
        empresaServicio.crearEmpresa(nombre, oferta);
        return "index.html";
    }
}
