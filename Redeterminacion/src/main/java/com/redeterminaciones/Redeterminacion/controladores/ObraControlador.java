package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.ClienteEmpresa;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.enumeraciones.Rubros;
import com.redeterminaciones.Redeterminacion.enumeraciones.TipoDeRedeterminaciones;
import com.redeterminaciones.Redeterminacion.servicios.ClienteEmpresaServicio;
import com.redeterminaciones.Redeterminacion.servicios.ComputoYPresupuestoServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import jakarta.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/obra")
public class ObraControlador {

    @Autowired
    private ObraServicio obraServicio;

    @Autowired
    private ComputoYPresupuestoServicio cyps;
    @Autowired
    private ClienteEmpresaServicio clienteEmpresaServicio;

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
                porcentajeDeAnticipo, 1, fecha2, tipoDeRedeterminaciones, null);
        ClienteEmpresa clienteEmpresa = (ClienteEmpresa) session.getAttribute("usuariosession");
        clienteEmpresaServicio.guardarObra(obra, clienteEmpresa.getEmail());
        return "index.html";
    }

    @PostMapping("/cyp")
    public String calculoCYP(HttpSession session, ModelMap map) {
//        cyps.crearComputoYPresupuesto(Rubros.HOLA, items);
        return "";
    }
}
