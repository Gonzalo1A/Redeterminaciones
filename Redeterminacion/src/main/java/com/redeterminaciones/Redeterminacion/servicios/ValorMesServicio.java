package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.ValorMes;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.redeterminaciones.Redeterminacion.repositorios.ValorMesRepositorio;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class ValorMesServicio {

    @Autowired
    private ValorMesRepositorio IOPfechaRepo;

    @Transactional
    public ValorMes crear(LocalDate fecha, Double valor) {
        ValorMes nuevo = new ValorMes();
        nuevo.setFecha(fecha);
        nuevo.setValor(valor);
        IOPfechaRepo.save(nuevo);
        return nuevo;
    }

    @Transactional
    public void modificarIndiceMensual(String id, LocalDate fecha, Double valor) {
        Optional<ValorMes> respuesta = IOPfechaRepo.findById(id);
        respuesta.ifPresent(indiceMensual -> {
            indiceMensual.setFecha(fecha);
            indiceMensual.setValor(valor);
            IOPfechaRepo.save(indiceMensual);
        });
    }

    @Transactional

    public void eliminarIndiceMensual(String id) {
        Optional<ValorMes> respuesta = IOPfechaRepo.findById(id);
        respuesta.ifPresent(indiceMensual -> {
            IOPfechaRepo.delete(indiceMensual);
        });
    }

    public ValorMes buscaIndiceMensual(String id) {
        return IOPfechaRepo.getById(id);
    }

    public LocalDate convertirStringALocalDate(String fechaStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        try {
            YearMonth yearMonth = YearMonth.parse(fechaStr, formatter);
            return yearMonth.atDay(1);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
