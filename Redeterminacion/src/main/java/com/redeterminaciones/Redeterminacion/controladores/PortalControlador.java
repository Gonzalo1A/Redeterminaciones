package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.ClienteEmpresa;
import com.redeterminaciones.Redeterminacion.entidades.Usuario;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.servicios.ClienteEmpresaServicio;
import com.redeterminaciones.Redeterminacion.servicios.ComputoYPresupuestoServicio;
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
    private ComputoYPresupuestoServicio computoYPresupuestoServicio;
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
        return "obrasList.html";
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

    @PostMapping("/registrarEmpresa")
    public String empresaGuardar(@RequestParam String nombre, @RequestParam Double oferta) {
        empresaServicio.crearEmpresa(nombre, oferta);
        return "index.html";
    }
}
