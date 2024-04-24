package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.IOP;
import com.redeterminaciones.Redeterminacion.entidades.IndiceMensual;
import com.redeterminaciones.Redeterminacion.repositorios.IOPRepositorio;
import jakarta.transaction.Transactional;
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
        IOP nueva = new IOP();
        nueva.setNombreFactor(nombre);
        iopRepo.save(nueva);
    }

    @Transactional
    public void agregarPorExel(Integer id, List<Date> fechas, List<Double> valores) {
        IOP factor = iopRepo.getById(id);
        List<IndiceMensual> tablas = null;
        for (int i = 0; i < fechas.size(); i++) {
            Date fecha = fechas.get(i);
            Double valor = valores.get(i);
            tablas.add(fechaSer.crear(fecha, valor));
        }
        factor.setFechas(tablas);
        iopRepo.save(factor);
    }
    
    public IOP buscarIndice(Integer orden){
        return iopRepo.getById(orden);
    }

    public List<IOP> todosLosIndices() {
        return iopRepo.findAll();
    }

}
