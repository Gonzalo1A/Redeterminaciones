package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.Usuario;
import com.redeterminaciones.Redeterminacion.enumeraciones.Roles;
import com.redeterminaciones.Redeterminacion.excepciones.RedeterminacionExcepcion;
import com.redeterminaciones.Redeterminacion.repositorios.UsuarioRepositorio;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UsuarioServicio implements UserDetailsService{

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional
    public void crearUsuario(String email, String password, String password2) throws RedeterminacionExcepcion {
        validar(email);
        validarPasword(password, password2);
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setRol(Roles.CLIENTE);
        usuarioRepositorio.save(usuario);
    }

    @Transactional
    public void modificar(String email) throws RedeterminacionExcepcion {
        validar(email);
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        usuario.setRol(Roles.CLIENTE);
        usuarioRepositorio.save(usuario);
    }

    public Usuario getUsuario(String id) {
        return usuarioRepositorio.getReferenceById(id);
    }

    public Usuario getUsuarioMail(String email) {
        return usuarioRepositorio.buscarPorEmail(email);
    }

    public List<Usuario> listaUsuarios() {
        return usuarioRepositorio.findAll();
    }

    public void cambiarPassword(Usuario usuario, String password, String passwordNuevo, String passwordNuevo2) throws RedeterminacionExcepcion {

        // BCryptPasswordEncoder es una clase que se utiliza comúnmente para realizar hashing y verificación de contraseñas seguras.
        if (usuario != null && new BCryptPasswordEncoder()
                .matches(password, usuario.getPassword())) {  //se procede a verificar si la contraseña proporcionada coincide con la contraseña almacenada en la base de datos. Esto se hace utilizando el método matches 

            if (passwordNuevo.equals(passwordNuevo2)) { //Si newPasword esta igual a equalPassword nos da acceso para poder encriptar la nueva contraseña
                usuario.setPassword(new BCryptPasswordEncoder()// Si la contraseña antigua coincide, esta línea establece la nueva contraseña proporcionada por el usuario después de codificarla con BCryptPasswordEncoder. Esto garantiza que la contraseña se almacene de forma segura en la base de datos.
                        .encode(passwordNuevo)); //encode: proporciona en la forma que se va a ingresar la contraseña, en este caso String
                usuarioRepositorio.save(usuario);
            } else {
                throw new RedeterminacionExcepcion("Las contraseñas nuevas no coinciden.");
            }

        } else {
            throw new RedeterminacionExcepcion("La contraseña antigua es incorrecta.");
        }

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);
        if (usuario != null) {
            List<GrantedAuthority> permisos = new ArrayList();
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
            permisos.add(p);
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);
            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        } else {
            return null;
        }
    }

    private void validar(String email) throws RedeterminacionExcepcion {
        //String regex = "^(?=.*[0-9])(?=.*[A-Z]).*$";
        if (email == null || email.trim().isEmpty()) {
            throw new RedeterminacionExcepcion("El Email no puede ser nulo o estar vacio");
        }

    }

    private void validarPasword(String password, String password2) throws RedeterminacionExcepcion { // Al cambiar la contraseña tiene que validarse 2 veces la nueva contraseña ingresada
        // Verifica que la contraseña no sea nula, esté vacía, tenga al menos 8 caracteres y cumpla con un patrón específico (números y mayúsculas)
        //|| password.length() <= 7 || !password.matches(regex) <-- validaciones faltantes
        if (password == null || password.trim().isEmpty()) {
            throw new RedeterminacionExcepcion("El password no puede ser nulo o estar vacio");
        }
        if (!password.equals(password2)) {
            throw new RedeterminacionExcepcion("El password tiene que ser igual en ambos campos");
        }
    }

}
