package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.IncidenciaFactor;
import com.redeterminaciones.Redeterminacion.repositorios.IncidenciaFactorRepositorio;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncidenciaFactorServicio {

    @Autowired
    private IncidenciaFactorRepositorio incidenciaFactorRepositorio;

    private final Pattern patron = Pattern.compile("(\\d+[,.]\\d+)\\s*?[Xx*.]\\s*?[Ff](\\d+)");

    @Transactional
    public IncidenciaFactor crearIncidenciaFactor(int factorReferencia, float porcentaje) {
        IncidenciaFactor incidenciaFactor = new IncidenciaFactor();
        incidenciaFactor.setIndice(factorReferencia);
        incidenciaFactor.setPorcentajeIncidencia(porcentaje);
        incidenciaFactorRepositorio.save(incidenciaFactor);
        return incidenciaFactor;
    }

    @Transactional
    public List<IncidenciaFactor> formatearValores(String cadena) {
        List<IncidenciaFactor> listIncidencia = new ArrayList<>();
        Matcher matcher = patron.matcher(cadena);
        while (matcher.find()) {
            String numeroStr = matcher.group(1);
            String porcentajeStr = numeroStr;
            String fValueStr = matcher.group(2);
            float porcentaje = Float.parseFloat(porcentajeStr);
            int numero = Integer.parseInt(fValueStr);
            IncidenciaFactor incFac = crearIncidenciaFactor(numero, porcentaje);
            listIncidencia.add(incFac);
        }
        return listIncidencia;

    }

    public IncidenciaFactor buscarIncidenciaFactor(String id) {
        return incidenciaFactorRepositorio.getReferenceById(id);
    }

    public void eliminarIncidenteFactor(String id) {
        incidenciaFactorRepositorio.deleteById(id);
    }
}
