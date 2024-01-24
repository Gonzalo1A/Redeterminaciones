package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.enumeraciones.TipoDeRedeterminaciones;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.redeterminaciones.Redeterminacion.servicios.EmpresaServicio;
import com.redeterminaciones.Redeterminacion.servicios.ItemServicio;
import com.redeterminaciones.Redeterminacion.servicios.ObraServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @GetMapping("/")
    public String index() {

        return "index.html";
    }

    @PostMapping("/registrarItem")
    public String itemGuardar(@RequestParam String numeroItem, @RequestParam String descripcion,
            @RequestParam String unidad, @RequestParam Double cantidad, @RequestParam Double subTotal) {
        itemServicio.crearItem(numeroItem, descripcion, unidad, cantidad, subTotal);
        return "index.html";
    }

    @PostMapping("/registrarObra")
    public String obraGuardar(@RequestParam String nombre, @RequestParam Double total,
            @RequestParam String fechaDeContrato, @RequestParam Double porcentajeDeAnticipo,
            @RequestParam String fechaDeFinalizacion, TipoDeRedeterminaciones tipoDeRedeterminaciones) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha1 = formato.parse(fechaDeContrato);
        Date fecha2 = formato.parse(fechaDeFinalizacion);
        obraServicio.crearObra(nombre, total, fecha1, fecha2, fecha2,
                porcentajeDeAnticipo, 1, fecha2, tipoDeRedeterminaciones, null);
        return "index.html";
    }

    @PostMapping("/registrarEmpresa")
    public String obraGuardar(@RequestParam String nombre, @RequestParam Double oferta) {
        empresaServicio.crearEmpresa(nombre, oferta);
        return "index.html";
    }
}
