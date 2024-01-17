package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.servicios.ItemServicio;
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
            
    @GetMapping("/")
    public String index() {
        
        return "index.html";
    }
    
    @PostMapping("/registrarItem")
    public String itemGuardar(@RequestParam String numeroItem, @RequestParam String descripcion,
            @RequestParam String unidad, @RequestParam Double cantidad){
        itemServicio.crearItem(numeroItem, descripcion, unidad, cantidad);
        return "index.html";
    }
    @PostMapping("/registrarItem")
    public String obraGuardar(@RequestParam String numeroItem, @RequestParam String descripcion,
            @RequestParam String unidad, @RequestParam Double cantidad){
        itemServicio.crearItem(numeroItem, descripcion, unidad, cantidad);
        return "index.html";
    }
}
