package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.ValorMes;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.redeterminaciones.Redeterminacion.repositorios.ValorMesRepositorio;
import com.redeterminaciones.Redeterminacion.utilidades.FechaUtilidades;
import java.time.LocalDate;

@Service
public class ValorMesServicio {

    @Autowired
    private ValorMesRepositorio valorMesRepo;

    @Transactional
    public ValorMes crear(LocalDate fecha, Double valor) {
        ValorMes nuevo = new ValorMes();
        nuevo.setFecha(fecha);
        nuevo.setValor(valor);
        valorMesRepo.save(nuevo);
        return nuevo;
    }

    @Transactional
    public void modificarIndiceMensual(String id, LocalDate fecha, Double valor) {
        Optional<ValorMes> respuesta = valorMesRepo.findById(id);
        respuesta.ifPresent(indiceMensual -> {
            indiceMensual.setFecha(fecha);
            indiceMensual.setValor(valor);
            valorMesRepo.save(indiceMensual);
        });
    }

    @Transactional
    public void eliminarIndiceMensual(String id) {
        Optional<ValorMes> respuesta = valorMesRepo.findById(id);
        respuesta.ifPresent(indiceMensual -> {
            valorMesRepo.delete(indiceMensual);
        });
    }

    public ValorMes buscaIndiceMensual(String id) {
        return valorMesRepo.getById(id);
    }

    public Double buscarValorPorFecha(String fecha, Integer iopID) {
        LocalDate fechaDate = FechaUtilidades.convertirStringALocalDateFormato2(fecha);
        return valorMesRepo.buscarValorPorFecha(iopID, fechaDate);
    }

}
