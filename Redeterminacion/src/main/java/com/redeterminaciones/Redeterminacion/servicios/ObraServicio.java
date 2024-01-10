package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.repositorios.ObraRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObraServicio {

    @Autowired
    private ObraRepositorio obraRepositorio;
}
