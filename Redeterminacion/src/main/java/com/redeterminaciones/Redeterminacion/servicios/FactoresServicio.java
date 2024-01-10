package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.repositorios.FactoresRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FactoresServicio {

    @Autowired
    private FactoresRepositorio factoresRepositorio;
}
