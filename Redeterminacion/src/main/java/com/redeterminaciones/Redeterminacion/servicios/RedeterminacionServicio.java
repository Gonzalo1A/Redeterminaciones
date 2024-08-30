package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.AvanceObraReal;
import com.redeterminaciones.Redeterminacion.entidades.IncidenciaFactor;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.Redeterminacion;
import com.redeterminaciones.Redeterminacion.entidades.ValorMes;
import com.redeterminaciones.Redeterminacion.repositorios.RedeterminacionRepositorio;
import com.redeterminaciones.Redeterminacion.utilidades.EstilosDeExel;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
            BigDecimal bd = new BigDecimal(factorRedet).setScale(4, RoundingMode.HALF_UP);
            double valorCon4Decimales = bd.doubleValue();
            return valorCon4Decimales;
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

    public ByteArrayInputStream exportarRepoteDeRedeterminacion(Obra obra, LocalDate mesSolicitud, LocalDate ultimaRedeterminacion) throws Exception {
        String[] columnas = {"Item", "Descripcion", "Unidad", "Cantidad", "Precio unitario previo", "Precio anterior", "Factor de redeterminacion", "Nuevo precio Unitario", "Remanente real", "Remanente teorico", "Menor Remanente", "Incremento de Precio Unit.", "Nuevo Precio"};
        ByteArrayOutputStream stream;
        try (XSSFWorkbook libro = new XSSFWorkbook()) {
            stream = new ByteArrayOutputStream();
            Sheet hoja = libro.createSheet("Redeterminacion");
            Row fila = hoja.createRow(0);
            //Creo los titulares del Reporte
            for (int i = 0; i < columnas.length; i++) {
                Cell celdaTitu = fila.createCell(i);
                celdaTitu.setCellValue(columnas[i]);
                celdaTitu.setCellStyle(EstilosDeExel.estiloEncabesados(libro));
            }
            int coordenadaRow = 1;
            for (Item item : obra.getItems()) {
                fila = hoja.createRow(coordenadaRow);
                Cell numItem = fila.createCell(0);
                numItem.setCellValue(item.getNumeroItem());
                numItem.setCellStyle(EstilosDeExel.estiloDatos(libro));

                Cell descripcion = fila.createCell(1);
                descripcion.setCellValue(item.getDescripcion());
                descripcion.setCellStyle(EstilosDeExel.estiloDatos(libro));

                if (!item.isRubro()) {
                    Cell unidad = fila.createCell(2);
                    unidad.setCellValue(item.getUnidad());
                    unidad.setCellStyle(EstilosDeExel.estiloDatos(libro));

                    Cell cantidad = fila.createCell(3);
                    cantidad.setCellValue(item.getCantidad());
                    cantidad.setCellStyle(EstilosDeExel.estiloDatos(libro));

                    Cell precioUn = fila.createCell(4);
                    precioUn.setCellValue(item.getPrecioUnitario());
                    precioUn.setCellStyle(EstilosDeExel.estiloMoneda(libro));

                    Cell subTotalAnt = fila.createCell(5);
                    subTotalAnt.setCellValue(item.getSubTotal());
                    subTotalAnt.setCellStyle(EstilosDeExel.estiloMoneda(libro));
                    //Calculo del factor de redeterminacion
                    Cell factorRed = fila.createCell(6);
                    factorRed.setCellValue(valorFactorRede(item, mesSolicitud, ultimaRedeterminacion));
                    factorRed.setCellStyle(EstilosDeExel.estiloDatos(libro));
                    //Calculo del nuevo Precio unitario
                    Cell nuevoPrecioUn = fila.createCell(7);
                    nuevoPrecioUn.setCellValue(item.getPrecioUnitario() * factorRed.getNumericCellValue());
                    nuevoPrecioUn.setCellStyle(EstilosDeExel.estiloMoneda(libro));

                    Double remanenteReal = remanenteDelAvanceReal(item, mesSolicitud);
                    Cell remReal = fila.createCell(8);
                    remReal.setCellValue(remanenteReal);
                    remReal.setCellStyle(EstilosDeExel.estiloDatos(libro));

                    Double remanenteTeorico = remanenteDelAvenceTeorico(item, mesSolicitud);
                    Cell remTeorico = fila.createCell(9);
                    remTeorico.setCellValue(remanenteTeorico);
                    remTeorico.setCellStyle(EstilosDeExel.estiloDatos(libro));
                    //Asigno el menor remanente
                    Cell menorRem = fila.createCell(10);
                    menorRem.setCellValue(remanenteTeorico);
                    if (remanenteReal < remanenteTeorico) {
                        menorRem.setCellValue(remanenteReal);
                    }
                    menorRem.setCellStyle(EstilosDeExel.estiloDatos(libro));
                    //Calculo del incremento del Sub Total
                    Double incremento = menorRem.getNumericCellValue() * (nuevoPrecioUn.getNumericCellValue() - precioUn.getNumericCellValue());
                    Cell incrementoSub = fila.createCell(11);
                    incrementoSub.setCellValue(incremento);
                    incrementoSub.setCellStyle(EstilosDeExel.estiloMoneda(libro));
                    //Suma de la diferencia de precio para el nuevo Sub Total
                    Cell nuevoSubtotal = fila.createCell(12);
                    nuevoSubtotal.setCellValue(item.getSubTotal() + incremento);
                    nuevoSubtotal.setCellStyle(EstilosDeExel.estiloMoneda(libro));
                } else {
                    CellRangeAddress rango = new CellRangeAddress(coordenadaRow, coordenadaRow, 2, 12);
                    for (int i = rango.getFirstColumn(); i <= rango.getLastColumn(); i++) {
                        Cell celdaRango = fila.createCell(i);
                        celdaRango.setCellStyle(EstilosDeExel.estiloDatos(libro));
                    }
                    hoja.addMergedRegion(rango);
                }
                coordenadaRow++;
            }
            libro.write(stream);
            libro.close();
            return new ByteArrayInputStream(stream.toByteArray());
        }
    }

    public byte[] convertInputStreamToByteArray(ByteArrayInputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    @Transactional
    public void guardarResumen(byte[] resumen, Integer id) {
        Optional<Redeterminacion> res = redeterminacionRepositorio.findById(id);
        if (res.isPresent()) {
            Redeterminacion redet = res.get();
            redet.setResumenRedet(resumen);
            redeterminacionRepositorio.save(redet);
        }
    }
}
