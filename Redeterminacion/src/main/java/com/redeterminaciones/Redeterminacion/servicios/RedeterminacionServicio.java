package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.AvanceObraReal;
import com.redeterminaciones.Redeterminacion.entidades.IncidenciaFactor;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.Redeterminacion;
import com.redeterminaciones.Redeterminacion.entidades.ValorMes;
import com.redeterminaciones.Redeterminacion.repositorios.RedeterminacionRepositorio;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedeterminacionServicio {

    @Autowired
    private RedeterminacionRepositorio redeterminacionRepositorio;
    @Autowired
    private IOPServicio iopServ;

    @Transactional
    public Redeterminacion crearRedeterminacion(LocalDate mesSolicitud, LocalDate mesOferta) {
        Redeterminacion redeterminacion = new Redeterminacion();
        redeterminacion.setMesSolicitud(mesSolicitud);
        redeterminacion.setMesSolictudAnterior(mesOferta);
        redeterminacionRepositorio.save(redeterminacion);
        return redeterminacion;
    }

    @Transactional
    public void modificar(Double remanente, Integer id) {
        Optional<Redeterminacion> res = redeterminacionRepositorio.findById(id);
        if (res.isPresent()) {
            Redeterminacion redeterminacionMod = res.get();
            redeterminacionMod.setVariacionReferencia(remanente);
            redeterminacionRepositorio.save(redeterminacionMod);
        }
    }

    public Redeterminacion buscarRedeterminacion(Integer id) {
        return redeterminacionRepositorio.getReferenceById(id);
    }

    public List<Redeterminacion> listaRedeterminacionesPorObra(Obra obra) {
        return obra.getRedeterminaciones();
    }

    @Transactional
    public void eliminarRedeterminacion(Integer id) {
        redeterminacionRepositorio.deleteById(id);
    }

    public Double calcularVR(Double ponderadorTotal, Double valMesBase, Double valMesAnterior) {
        if (valMesBase == 0.0d) {
            return 0.0d;
        }
        return (valMesAnterior / valMesBase) * ponderadorTotal;
    }

    public List<Double> factoresRedet(Obra obra, Redeterminacion redet) {
        List<Double> factores = new ArrayList<>();
        for (Item item : obra.getItems()) {
            factores.add(valorFactorRede(item, redet.getMesSolicitud(), redet.getMesSolictudAnterior()));
        }
        return factores;
    }

    private Double valorFactorRede(Item item, LocalDate mesSolicitud, LocalDate mesAnterior) {
        Double factorRedet = 0.0d;
        if (!item.isRubro()) {
            for (IncidenciaFactor inFac : item.getIncidenciaFactores()) {
                Double indiceNuevo = iopServ.getValorPorMes(mesSolicitud, inFac.getIndice());
                Double indiceBase = iopServ.getValorPorMes(mesAnterior, inFac.getIndice());
                
                factorRedet += calcularVR(inFac.getPorcentajeIncidencia(), indiceBase, indiceNuevo);
            }
            return factorRedet;
        }
        return null;
    }

    public List<Double> listaRemanenteTeorico(List<Item> items, LocalDate fechaDeSolicitud) {
        List<Double> listaRes = new ArrayList<>();
        for (Item item : items) {
            listaRes.add(remanenteDelAvenceTeorico(item, fechaDeSolicitud));
        }
        return listaRes;
    }

    private Double remanenteDelAvenceTeorico(Item item, LocalDate fechaDeSolicitud) {
        if (!item.isRubro()) {
            List<ValorMes> avanceTeorico = item.getAvanceTeorico();
            Double acumulado = 0.0;
            Double cantidad = item.getCantidad();
            //Llevo la fecha al ultimo dia del mes
            fechaDeSolicitud = fechaDeSolicitud.withDayOfMonth(fechaDeSolicitud.lengthOfMonth());
            if (avanceTeorico != null && !avanceTeorico.isEmpty()) {
                Collections.sort(avanceTeorico);
                for (ValorMes valorMes : avanceTeorico) {
                    if (valorMes.getFecha().isBefore(fechaDeSolicitud)) {
                        acumulado += valorMes.getValor();
                    }
                }
            }
            return cantidad - acumulado;
        }
        return null;
    }

    public List<Double> listaRemanenteReal(List<Item> items, LocalDate fechaDeSolicitud) {
        List<Double> listaRes = new ArrayList<>();
        for (Item item : items) {
            listaRes.add(remanenteDelAvanceReal(item, fechaDeSolicitud));
        }
        return listaRes;
    }

    private Double remanenteDelAvanceReal(Item item, LocalDate fechaDeSolicitud) {
        if (!item.isRubro()) {
            List<AvanceObraReal> avanceReal = item.getAvanceObraReal();
            Double cantidad = item.getCantidad();
            //Llevo la fecha al ultimo dia del mes
            fechaDeSolicitud.minusMonths(1);
            fechaDeSolicitud = fechaDeSolicitud.withDayOfMonth(fechaDeSolicitud.lengthOfMonth());
            if (!avanceReal.isEmpty()) {
                for (AvanceObraReal avanceObraReal : avanceReal) {
                    LocalDate fechaDelAvance = avanceObraReal.getValorMes().getFecha();
                    fechaDelAvance = fechaDelAvance.withDayOfMonth(fechaDelAvance.lengthOfMonth());
                    if (fechaDeSolicitud.equals(fechaDelAvance)) {
                        return cantidad - avanceObraReal.getAcumuladoActual();
                    } else {
                        return 0.0;
                    }
                }
            } else {
                return 0.0;
            }
        }
        return null;
    }

    public List<Double> menorRemanentes(List<Double> remanenteTeorico, List<Double> remanenteReal) {
        List<Double> listaMinimos = new ArrayList<>();
        for (int i = 0; i < remanenteTeorico.size(); i++) {
            Double valor1 = remanenteTeorico.get(i);
            Double valor2 = remanenteReal.get(i);
            if (valor1 != null && valor2 != null) {
                listaMinimos.add(Math.min(valor1, valor2));
            } else {
                listaMinimos.add(null);
            }
        }
        return listaMinimos;
    }

    public List<Double> listaIncrementosSubTotal(List<Item> items, List<Double> factoresRedet, List<Double> menorRemanente) {
        List<Double> listaRes = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isRubro()) {
                listaRes.add(redeterminacionDePrecio(items.get(i), factoresRedet.get(i), menorRemanente.get(i)));
            } else {
                listaRes.add(null);
            }
        }
        return listaRes;
    }

    private Double redeterminacionDePrecio(Item item, Double factorDeDeterminacion, Double menorRemanente) {
        if (!item.isRubro()) {
            Double nuevoPrecioUn = calculoNuevoPrecioUnitario(item.getPrecioUnitario(), factorDeDeterminacion);
            Double incrementoDelSubTotal = menorRemanente * (nuevoPrecioUn - item.getPrecioUnitario());
            return incrementoDelSubTotal;
        }
        return null;
    }

    public List<Double> listaPreciosUnitariosNuevos(List<Item> items, List<Double> factoresRedet) {
        List<Double> listaRes = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isRubro()) {
                listaRes.add(calculoNuevoPrecioUnitario(items.get(i).getPrecioUnitario(), factoresRedet.get(i)));
            } else {
                listaRes.add(null);
            }
        }
        return listaRes;
    }

    public Double calculoNuevoPrecioUnitario(Double precioViejo, Double factorRedet) {
        return precioViejo * factorRedet;
    }

}
