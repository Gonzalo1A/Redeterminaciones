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
import com.redeterminaciones.Redeterminacion.servicios.ExelServicio;
import com.redeterminaciones.Redeterminacion.servicios.ItemServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
    @Autowired
    private ExelServicio exelServicio;

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
        ByteArrayInputStream stream = exelServicio.elExportador();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=items.xlsx");
        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }

//    @PostMapping("/importItem")
//    public String importar(@RequestParam File fileExcel) throws Exception{
//        File achivo = new File("C:/Users/Octavio/Documents/items.xlsx");
//        exelServicio.elImportador(achivo);
//       
//        return "redirect:/listaItems";
//    }
    @PostMapping("/importItem")
    public String importar(@RequestParam("fileExcel") MultipartFile fileExcel) {
        try {
            exelServicio.elImportador(fileExcel.getInputStream());
            return "redirect:/listaItems";
        } catch (Exception e) {
            // Manejar la excepción adecuadamente (mostrar mensaje de error, redirigir a página de error, etc.)
            System.out.println(e.getMessage());
            return "redirect:/listaItems";
        }
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
