package com.redeterminaciones.Redeterminacion.controladores;

import com.redeterminaciones.Redeterminacion.entidades.ClienteEmpresa;
import com.redeterminaciones.Redeterminacion.entidades.Usuario;
import com.redeterminaciones.Redeterminacion.servicios.ClienteEmpresaServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PortalControlador {

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
        return "obrasList.html";
    }

}
