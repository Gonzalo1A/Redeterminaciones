package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.AvanceObraReal;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.ValorMes;
import com.redeterminaciones.Redeterminacion.repositorios.AvanceRealRepositorio;
import com.redeterminaciones.Redeterminacion.utilidades.EstilosDeExel;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvanceRealServicio {

    @Autowired
    private AvanceRealRepositorio avanceRealRepositorio;
    @Autowired
    private ValorMesServicio valorMesServicio;

    @Transactional
    public AvanceObraReal crearAvanceReal() {
        AvanceObraReal avanceObra = new AvanceObraReal();
        avanceObra.setAcumuladoAnterior(0d);
        avanceRealRepositorio.save(avanceObra);
        return avanceObra;
    }

    public List<AvanceObraReal> cargarAvance(Item item, ValorMes valorMes) {
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
        return listaAvance;
    }

    public AvanceObraReal getAvanceObraReal(String id) {
        return avanceRealRepositorio.getReferenceById(id);
    }

    public List<AvanceObraReal> listaUsuarios() {
        return avanceRealRepositorio.findAll();
    }

    public ByteArrayInputStream exportarModeloDeCargaDeAvanceRealExcel(Obra obra, LocalDate fechaObjetiva) throws Exception {
        String[] columnas = {"ID", "Nro", "DESCRIPCION", "UNIDAD", "CANTIDAD", "PRECIO", "Cantidad certificada a la fecha"};
        List<Item> items = obra.getItems();
        ByteArrayOutputStream stream;
        try (XSSFWorkbook libro = new XSSFWorkbook()) {
            stream = new ByteArrayOutputStream();
            Sheet hoja = libro.createSheet("Ingreso de Avance Real");
            Row fila = hoja.createRow(0);

            for (int i = 0; i < columnas.length; i++) {
                Cell celda = fila.createCell(i);
                celda.setCellValue(columnas[i]);
                celda.setCellStyle(EstilosDeExel.estiloEncabesados(libro));
            }
            fila.createCell(7, CellType.NUMERIC).setCellValue(fechaObjetiva);
            fila.getCell(7).setCellStyle(EstilosDeExel.estiloEncabesados(libro));
            int coordenadaRow = 1;
            for (Item item : items) {
                fila = hoja.createRow(coordenadaRow);

                Cell id = fila.createCell(0);
                id.setCellValue(item.getId());

                Cell numItem = fila.createCell(1);
                numItem.setCellValue(item.getNumeroItem());

                Cell descripcion = fila.createCell(2);
                descripcion.setCellValue(item.getDescripcion());

                Cell unidad = fila.createCell(3);
                unidad.setCellValue(item.getUnidad());

                Cell cantidad = fila.createCell(4);
                Cell precio = fila.createCell(5);
                Cell cantAFecha = fila.createCell(6);
                Cell avanceMes = fila.createCell(7);

                if (item.getCantidad() != null && item.getPrecioUnitario() != null && item.getSubTotal() != null) {
                    cantidad.setCellValue(item.getCantidad());
                    precio.setCellValue(item.getSubTotal());
                    List<AvanceObraReal> avanceALaFecha = item.getAvanceObraReal();
                    if (!avanceALaFecha.isEmpty()) {
                        for (AvanceObraReal avanceObraReal : avanceALaFecha) {
                            int anio1 = fechaObjetiva.getYear();
                            int mes1 = fechaObjetiva.getMonthValue();
                            int aniob = avanceObraReal.getValorMes().getFecha().getYear();
                            int mesb = avanceObraReal.getValorMes().getFecha().getMonthValue();
                            if (anio1 == aniob && mes1 == mesb) {
                                cantAFecha.setCellValue(avanceObraReal.getAcumuladoAnterior());
                                if (avanceObraReal.getValorMes().getValor() != null) {
                                    avanceMes.setCellValue(avanceObraReal.getValorMes().getValor());
                                }
                            }
                        }
                    }
                } else {
                    CellRangeAddress rango = new CellRangeAddress(coordenadaRow, coordenadaRow, 3, 7);
//                    for (int i = rango.getFirstColumn(); i <= rango.getLastColumn(); i++) {
//                        Cell celdaRango = fila.createCell(i);
//                        celdaRango.setCellStyle(estiloDatos);
//                    }
                    hoja.addMergedRegion(rango);
                }
                coordenadaRow++;
            }
            libro.write(stream);
            libro.close();
        }
        return new ByteArrayInputStream(stream.toByteArray());
    }

}
