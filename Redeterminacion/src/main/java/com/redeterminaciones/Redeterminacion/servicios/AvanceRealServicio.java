package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.AvanceObraReal;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.ValorMes;
import com.redeterminaciones.Redeterminacion.repositorios.AvanceRealRepositorio;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
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

    public ByteArrayInputStream exportarModeloDeCargaDeAvanceRealExcel(Obra obra, Date fechaObjetiva) throws Exception {
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
            }
            fila.createCell(7, CellType.NUMERIC).setCellValue(fechaObjetiva);

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
                        Calendar cal1 = Calendar.getInstance();
                        Calendar cal2 = Calendar.getInstance();
                        for (AvanceObraReal avanceObraReal : avanceALaFecha) {
                            cal1.setTime(fechaObjetiva);
                            cal2.setTime(avanceObraReal.getValorMes().getFecha());
                            int anio1 = cal1.get(Calendar.YEAR);
                            int mes1 = cal1.get(Calendar.MONTH);
                            int anio2 = cal2.get(Calendar.YEAR);
                            int mes2 = cal2.get(Calendar.MONTH);
                            if (anio1 == anio2 && mes1 == mes2) {
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

    public void importarAvnaceRealMensualPorExel(InputStream archivo) throws IOException {
        try (XSSFWorkbook libro = new XSSFWorkbook(archivo)) {
            Sheet hoja = libro.getSheetAt(0);
            Date fecha = null;
            for (Row fila : hoja) {
                Long id = null;
                Double valor = null;
                Cell celdaId = fila.getCell(0);
                Cell celdaOcho = fila.getCell(7);
                if (DateUtil.isCellDateFormatted(celdaOcho)) {
                    fecha = celdaOcho.getDateCellValue();
                }
                if (celdaId.getCellType() == CellType.NUMERIC) {
                    id = (long) celdaId.getNumericCellValue();
                    valor = celdaOcho.getNumericCellValue();
                }
                if (id != null && valor != null && fecha != null) {
                    
                }
            }
            libro.close();
        }
    }
}
