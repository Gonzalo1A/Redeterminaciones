package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.Redeterminacion;
import com.redeterminaciones.Redeterminacion.entidades.ValorMes;
import com.redeterminaciones.Redeterminacion.repositorios.RedeterminacionRepositorio;
import com.redeterminaciones.Redeterminacion.utilidades.EstilosDeExel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedeterminacionServicio {

    @Autowired
    private RedeterminacionRepositorio redeterminacionRepositorio;

    public void crearRedeterminacion(LocalDate mesSolicitud) {
        Redeterminacion redeterminacion = new Redeterminacion();
        redeterminacion.setMesSolicitud(mesSolicitud);
        redeterminacionRepositorio.save(redeterminacion);
    }

    public ByteArrayInputStream exportarModeloParaAvanceDeObraTeoricoExcel(Obra obra) throws Exception {
        String[] columnas = {"Nro", "Descripcion de Item", "Unidad", "Precio unitario anterior", "Precio " ,"Factor de Redeterminacion","Remanente a Ejecutar","PRECIO UNITARIO", "PRECIO"};
        List<Item> todos = obra.getItems();
        ByteArrayOutputStream stream;
        try (XSSFWorkbook libro = new XSSFWorkbook()) {
            stream = new ByteArrayOutputStream();
            Sheet hoja = libro.createSheet("Avance de obra teorico");
            Row fila = hoja.createRow(0);
            XSSFCellStyle encabesadosFechas = EstilosDeExel.estiloEncabesados(libro);
            encabesadosFechas.setDataFormat(libro.createDataFormat().getFormat("MM-yyyy"));
            XSSFCellStyle estiloDatos = EstilosDeExel.estiloDatos(libro);
            XSSFCellStyle estiloMoneda = EstilosDeExel.estiloMoneda(libro);
            XSSFCellStyle estiloRubros = EstilosDeExel.estiloDatos(libro);
            estiloRubros.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
            estiloRubros.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFFont fuente = libro.createFont();
            fuente.setBold(true);
            fuente.setColor(IndexedColors.WHITE.getIndex());
            XSSFCellStyle estiloAzul = EstilosDeExel.estiloDatos(libro);
            estiloAzul.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            estiloAzul.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloAzul.setFont(fuente);
            XSSFCellStyle estiloCeleste = EstilosDeExel.estiloDatos(libro);
            estiloCeleste.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
            estiloCeleste.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < columnas.length; i++) {
                Cell celda = fila.createCell(i);
                celda.setCellValue(columnas[i]);
                celda.setCellStyle(EstilosDeExel.estiloEncabesados(libro));
            }

            List<LocalDate> mesesDeObra = new ArrayList<>();
//            Calendar calendar = Calendar.getInstance();
            LocalDate fechaInicio = obra.getFechaDeReeplanteo();
//            calendar.setTime(fechaInicio);
//            calendar.add(Calendar.DAY_OF_MONTH, obra.getDiasPlazoDeObra());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            Date fechaFinal = calendar.getTime();

//            LocalDate inicio = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1).plusMonths(1);
            LocalDate fin = obra.getFechaDeFinalizacion();

            YearMonth comienso = YearMonth.from(fechaInicio);
            YearMonth finalisima = YearMonth.from(fin);
            YearMonth mesActual = comienso;

            while (!mesActual.isAfter(finalisima)) {
                LocalDate ultimoDia = mesActual.atEndOfMonth();
//                Date convierte = (Date.from(ultimoDia.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                mesesDeObra.add(ultimoDia);
                mesActual = mesActual.plusMonths(1);
            }

            int fechaCoordenada = 0;
            for (int i = 7; i < mesesDeObra.size() + 7; i++) {
                Cell celda = fila.createCell(i);
                celda.setCellValue(mesesDeObra.get(fechaCoordenada));
                celda.setCellStyle(encabesadosFechas);
                fechaCoordenada++;
            }

            int coordenadaRow = 1;
            boolean banderaDeEstilos = true;
            for (Item item : todos) {
                if (todos.get(coordenadaRow - 1).isRubro()) {
                    banderaDeEstilos = false;
                }
                fila = hoja.createRow(coordenadaRow);

                Cell id = fila.createCell(0);
                id.setCellValue(item.getId());
                id.setCellStyle(estiloDatos);

                Cell numItem = fila.createCell(1);
                numItem.setCellValue(item.getNumeroItem());
                numItem.setCellStyle(estiloDatos);

                Cell descripcion = fila.createCell(2);
                descripcion.setCellValue(item.getDescripcion());
                descripcion.setCellStyle(estiloDatos);

                Cell unidad = fila.createCell(3);
                unidad.setCellValue(item.getUnidad());
                unidad.setCellStyle(estiloDatos);

                if (item.getCantidad() != null && item.getPrecioUnitario() != null && item.getSubTotal() != null) {
                    Cell cantidad = fila.createCell(4);
                    cantidad.setCellValue(item.getCantidad());
                    cantidad.setCellStyle(estiloDatos);

                    Cell precioUn = fila.createCell(5);
                    precioUn.setCellValue(item.getPrecioUnitario());
                    precioUn.setCellStyle(estiloMoneda);

                    Cell subTotal = fila.createCell(6);
                    subTotal.setCellValue(item.getSubTotal());
                    subTotal.setCellStyle(estiloMoneda);

                    for (int i = 7; i < mesesDeObra.size() + 7; i++) {
                        Cell celdaDeCarga = fila.createCell(i);
                        celdaDeCarga.setCellStyle(estiloCeleste);
                        if (banderaDeEstilos) {
                            celdaDeCarga.setCellStyle(estiloAzul);
                        }
                    }
                    List<ValorMes> avanceTeocratico = item.getAvanceTeorico();
                    if (!avanceTeocratico.isEmpty()) {
                        for (ValorMes valorMes : avanceTeocratico) {
                            Row filaTitular = hoja.getRow(0);
                            String fechaAvance = formatter.format(valorMes.getFecha());
                            for (Cell cell : filaTitular) {
                                if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                                    String titulos = formatter.format(cell.getDateCellValue());
                                    System.out.println(titulos + "    separador    " + fechaAvance);
                                    if (titulos.equals(fechaAvance)) {
                                        fila.getCell(cell.getColumnIndex()).setCellValue(valorMes.getValor());
                                    }
                                }
                            }
                        }
                    }
                    banderaDeEstilos = !banderaDeEstilos;
                } else {
                    numItem.setCellStyle(estiloRubros);
                    descripcion.setCellStyle(estiloRubros);
                    CellRangeAddress rango = new CellRangeAddress(coordenadaRow, coordenadaRow, 3, 6 + mesesDeObra.size());
                    for (int i = rango.getFirstColumn(); i <= rango.getLastColumn(); i++) {
                        Cell celdaRango = fila.createCell(i);
                        celdaRango.setCellStyle(estiloRubros);
                    }
                    hoja.addMergedRegion(rango);
                }
                coordenadaRow++;
            }
            hoja.setColumnWidth(0, 1);
            libro.write(stream);
            libro.close();
        }
        return new ByteArrayInputStream(stream.toByteArray());
    }

}
