package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.servicios.ComputoYPresupuestoServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/computo")
public class CyPControlador {
    @Autowired
    private ComputoYPresupuestoServicio computoYPresupuestoServicio;
    
    @GetMapping("/")
    public String computoYPresupuesto(ModelMap map, HttpSession session){
        
        
        
        return "obrasView.html";
    }
}
