package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.excepciones.RedeterminacionExcepcion;
import com.redeterminaciones.Redeterminacion.servicios.ClienteEmpresaServicio;
import com.redeterminaciones.Redeterminacion.servicios.UsuarioServicio;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private ClienteEmpresaServicio clienteEmpresaServicio;

    @GetMapping("/login")
    public String login() {
        return "login.html";
    }

    @GetMapping("/registrar")
    public String registrar() {
        return "registroUsuario.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String email, @RequestParam String password,
            @RequestParam String password2, String nombreEmpresa,
            String direccion, String numeroCuit, ModelMap model) {

        try {
            //usuarioServicio.crearUsuario(email, password, password2, nombreEmpresa, direccion, numeroCuit);
            clienteEmpresaServicio.crearCliente(email, password, password2, nombreEmpresa, direccion, numeroCuit);
            return "index.html";
        } catch (RedeterminacionExcepcion ex) {
            Logger.getLogger(UsuarioControlador.class.getName()).log(Level.SEVERE, null, ex);
            return "registroUsuario.html";
        }

    }
  
    

}
