package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.repositorios.ItemRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemServicio {
    @Autowired
    private ItemRepositorio itemRepositorio;
}
