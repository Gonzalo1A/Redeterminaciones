package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.IndiceMensual;
import jakarta.transaction.Transactional;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.redeterminaciones.Redeterminacion.repositorios.IndiceMensualRepositorio;
import java.util.Optional;

@Service
public class IndiceMensualServicio {

    @Autowired
    private IndiceMensualRepositorio IOPfechaRepo;

    @Transactional
    public IndiceMensual crear(Date fecha, Double valor) {
        IndiceMensual nuevo = new IndiceMensual();
        nuevo.setFecha(fecha);
        nuevo.setValor(valor);
        IOPfechaRepo.save(nuevo);
        return nuevo;
    }

    @Transactional
    public void modificarIndiceMensual(String id, Date fecha, Double valor) {
        Optional<IndiceMensual> respuesta = IOPfechaRepo.findById(id);
        respuesta.ifPresent(indiceMensual -> {
            indiceMensual.setFecha(fecha);
            indiceMensual.setValor(valor);
            IOPfechaRepo.save(indiceMensual);
        });
    }
    
    @Transactional
    public void eliminarIndiceMensual(String id){
        Optional<IndiceMensual> respuesta = IOPfechaRepo.findById(id);
        respuesta.ifPresent(indiceMensual -> {
            IOPfechaRepo.delete(indiceMensual);
        });
    }

    public IndiceMensual buscaIndiceMensual(String id) {
        return IOPfechaRepo.getById(id);
    }

}
