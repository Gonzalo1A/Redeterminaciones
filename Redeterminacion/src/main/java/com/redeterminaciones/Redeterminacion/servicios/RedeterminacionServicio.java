package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.repositorios.RedeterminacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedeterminacionServicio {
    @Autowired
    private RedeterminacionRepositorio redeterminacionRepositorio;
}
