package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.servicios.EmpresaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {
    @Autowired
    private EmpresaServicio empresaServicio;
    
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
