package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.ClienteEmpresa;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.enumeraciones.Roles;
import com.redeterminaciones.Redeterminacion.excepciones.RedeterminacionExcepcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.redeterminaciones.Redeterminacion.repositorios.ClienteEmpresaRepositorio;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class ClienteEmpresaServicio {

    @Autowired
    private ClienteEmpresaRepositorio clienteEmpresaRepositorio;

    @Transactional
    public void crearCliente(String email, String password, String password2, String empresa,
            String direccion, String numeroCuit) throws RedeterminacionExcepcion {
        validar(email);
        validarPasword(password, password2);
        ClienteEmpresa cliente = new ClienteEmpresa();
        cliente.setEmail(email);
        cliente.setPassword(new BCryptPasswordEncoder().encode(password));
        cliente.setRol(Roles.CLIENTE);
        cliente.setEmpresaNombre(empresa);
        cliente.setDireccion(direccion);
        cliente.setNumeroCuit(numeroCuit);
        clienteEmpresaRepositorio.save(cliente);
    }

    @Transactional
    public void guardarObra(Obra obra, String email) {
        ClienteEmpresa clienteEmpresa = clienteEmpresaRepositorio.buscarPorEmail(email);
        List<Obra> obras = clienteEmpresa.getObras();
        obras.add(obra);
        clienteEmpresaRepositorio.save(clienteEmpresa);
    }

    public ClienteEmpresa getCliente(String id) {
        return clienteEmpresaRepositorio.getReferenceById(id);
    }

    @Transactional
    public void modificarCLiente(String id, String email, String empresa, String direccion,
            String numeroCuit) throws RedeterminacionExcepcion {
        validar(email);
        Optional<ClienteEmpresa> respuesta = clienteEmpresaRepositorio.findById(id);
        if (respuesta.isPresent()) {
            ClienteEmpresa cliente = respuesta.get();
            cliente.setEmpresaNombre(empresa);
            cliente.setDireccion(direccion);
            cliente.setNumeroCuit(numeroCuit);
            clienteEmpresaRepositorio.save(cliente);
        }
    }

    private void validar(String email) throws RedeterminacionExcepcion {
        //String regex = "^(?=.*[0-9])(?=.*[A-Z]).*$";
        if (email == null || email.trim().isEmpty()) {
            throw new RedeterminacionExcepcion("El Email no puede ser nulo o estar vacio");
        }

    }

    private void validarPasword(String password, String password2) throws RedeterminacionExcepcion { // Al cambiar la contraseña tiene que validarse 2 veces la nueva contraseña ingresada

        if (password == null || password.trim().isEmpty()) {
            throw new RedeterminacionExcepcion("El password no puede ser nulo o estar vacio");
        }
        if (!password.equals(password2)) {
            throw new RedeterminacionExcepcion("El password tiene que ser igual en ambos campos");
        }
    }
}
