package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.IndiceMensual;
import jakarta.transaction.Transactional;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.redeterminaciones.Redeterminacion.repositorios.IndiceMensualRepositorio;

@Service
public class IndiceMensualServicio {

    @Autowired
    private IndiceMensualRepositorio IOPfechaRepo;

    @Transactional
    public IndiceMensual crear(Date fecha, Double valor) {
        IndiceMensual nuevo= new IndiceMensual();
        nuevo.setFecha(fecha);
        nuevo.setValor(valor);
        //IOPfechaRepo.save(nuevo);
        return nuevo;
    }
}
