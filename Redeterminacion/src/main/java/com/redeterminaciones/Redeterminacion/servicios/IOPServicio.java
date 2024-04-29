package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.IOP;
import com.redeterminaciones.Redeterminacion.entidades.IndiceMensual;
import com.redeterminaciones.Redeterminacion.repositorios.IOPRepositorio;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IOPServicio {

    @Autowired
    private IOPRepositorio iopRepo;
    @Autowired
    private IndiceMensualServicio fechaSer;

    @Transactional
    public void crearIOP(String nombre) {
        IOP op = iopRepo.buscarObraPorNombre(nombre);
        if (op == null) {
            IOP nueva = new IOP();
            nueva.setNombreFactor(nombre);
            iopRepo.save(nueva);
        }
    }

    @Transactional
    public void agregarPorExel(Integer id, List<Date> fechas, List<Double> valores) {
        IOP factor = iopRepo.getById(id);
        List<IndiceMensual> tablas = new ArrayList<>();
        for (int i = 0; i < fechas.size(); i++) {
            Date fecha = fechas.get(i);
            Double valor = valores.get(i);
            tablas.add(fechaSer.crear(fecha, valor));
        }
        iopRepo.save(factor);
        factor.setFechas(tablas);
    }

    public IOP buscarFactorPorNombre(String nombreFactor) {
        return iopRepo.buscarObraPorNombre(nombreFactor);
    }

    public IOP buscarIndice(Integer orden) {
        return iopRepo.getById(orden);
    }

    public List<IOP> todosLosIndices() {
        return iopRepo.findAll();
    }

    public IOP buscarIOP(int id) {
        return iopRepo.getReferenceById(id);
    }

}
