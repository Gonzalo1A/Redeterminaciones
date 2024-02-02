package com.redeterminaciones.Redeterminacion.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    
    @GetMapping("/registrar/empresa")
    public String registrarEmpresa(){
        return "formEmpresa.html";
    }
    
    @GetMapping("/registrar/obra")
    public String registrarObra(){
        return "formObra.html";
    }
    
    @GetMapping("/registrar/item")
    public String registrarItem(){
        return "formItem.html";
    }
    
    
}
