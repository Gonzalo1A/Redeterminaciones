package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.AvanceObraReal;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.ValorMes;
import com.redeterminaciones.Redeterminacion.repositorios.AvanceRealRepositorio;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvanceRealServicio {

    @Autowired
    private AvanceRealRepositorio avanceRealRepositorio;

    @Transactional
    public AvanceObraReal crearAvanceReal() {
        AvanceObraReal avanceObra = new AvanceObraReal();
        avanceObra.setAcumuladoAnterior(0d);
        avanceRealRepositorio.save(avanceObra);
        return avanceObra;
    }

    public void cargarAvance(Item item, ValorMes valorMes) {
        AvanceObraReal avanceObraReal = crearAvanceReal();
        List<AvanceObraReal> listaAvance = item.getAvanceObraReal();
        avanceObraReal.setValorMes(valorMes);
        if (listaAvance != null && listaAvance.size() > 1) {
            int aux = listaAvance.size() - 1;
            avanceObraReal.setAcumuladoAnterior(listaAvance.get(aux).getAcumuladoActual());
            avanceObraReal.setAcumuladoActual(avanceObraReal.getAcumuladoAnterior() + valorMes.getValor());
        } else {
            avanceObraReal.setAcumuladoActual(valorMes.getValor());
        }
        listaAvance.add(avanceObraReal);
        avanceRealRepositorio.save(avanceObraReal);
    }

    public AvanceObraReal getAvanceObraReal(String id) {
        return avanceRealRepositorio.getReferenceById(id);
    }

    public List<AvanceObraReal> listaUsuarios() {
        return avanceRealRepositorio.findAll();
    }
}
