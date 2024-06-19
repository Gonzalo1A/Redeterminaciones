package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.Redeterminacion;
import com.redeterminaciones.Redeterminacion.repositorios.RedeterminacionRepositorio;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedeterminacionServicio {
    @Autowired
    private RedeterminacionRepositorio redeterminacionRepositorio;
    
    public void crearRedeterminacion(LocalDate mesSolicitud){
        Redeterminacion redeterminacion = new Redeterminacion();
        redeterminacion.setMesSolicitud(mesSolicitud);
        redeterminacionRepositorio.save(redeterminacion);
    }
}
