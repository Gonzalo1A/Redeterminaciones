package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.IOP;
import com.redeterminaciones.Redeterminacion.entidades.IndiceMensual;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExelServicio {

    @Autowired
    private ItemServicio itemServi;
    @Autowired
    private ObraServicio obraServicio;
    @Autowired
    private IOPServicio iopServicio;

    public List<Item> elImportador(InputStream archivo) throws Exception {
//        try (InputStream input = new FileInputStream(archivo)) {
//            XSSFWorkbook libro = new XSSFWorkbook(input);
        List<Item> items = new ArrayList<>();
        XSSFWorkbook libro = new XSSFWorkbook(archivo);

        Sheet hoja = libro.getSheetAt(0);

        for (int i = 1; i <= hoja.getLastRowNum(); i++) {
            Row filaActual = hoja.getRow(i);
            if (filaActual != null) {
                String numItem = "";
                String descripcion = "";
                String unidad = "";
                Double cantidad = null;
                Double precioUnitario = null;

                Cell celdaNumItem = filaActual.getCell(0);
                if (celdaNumItem != null) {
                    if (celdaNumItem.getCellType() == CellType.STRING) {
                        numItem = celdaNumItem.getStringCellValue();
                    } else if (celdaNumItem.getCellType() == CellType.NUMERIC) {
                        numItem = String.valueOf((int) celdaNumItem.getNumericCellValue());
                    }
                }

                Cell celdaDescripcion = filaActual.getCell(1);
                if (celdaDescripcion != null && celdaDescripcion.getCellType() == CellType.STRING) {
                    descripcion = celdaDescripcion.getStringCellValue();
                }

                Cell celdaUnidad = filaActual.getCell(2);
                if (celdaUnidad != null && celdaUnidad.getCellType() == CellType.STRING) {
                    unidad = celdaUnidad.getStringCellValue();
                }

                Cell celdaCantidad = filaActual.getCell(3);
                if (celdaCantidad != null && celdaCantidad.getCellType() == CellType.NUMERIC) {
                    cantidad = celdaCantidad.getNumericCellValue();
                } else if (celdaCantidad == null) {

                }

                Cell celdaPrecioUnitario = filaActual.getCell(4);
                if (celdaPrecioUnitario != null && celdaPrecioUnitario.getCellType() == CellType.NUMERIC) {
                    precioUnitario = celdaPrecioUnitario.getNumericCellValue();
                }
                if (numItem != "" && descripcion != "") {
                    Item item = itemServi.crearItem(numItem, descripcion, unidad, cantidad, precioUnitario);
                    items.add(item);
                }
            }
        }
        libro.close();
        return items;
    }

    public ByteArrayInputStream elExportador(String nombreObra) throws Exception {
        String[] columnas = {"Nro", "Descripcion", "Unidad", "Cantidad", "Precio Unitario", "Sub Total"};
        ByteArrayOutputStream stream;
        List<Item> todos = obraServicio.buscarPorNombre(nombreObra).getItems();

        try (XSSFWorkbook libro = new XSSFWorkbook()) {
            stream = new ByteArrayOutputStream();
            org.apache.poi.ss.usermodel.Sheet hoja = libro.createSheet("Items");
            Row fila = hoja.createRow(0);
            XSSFCellStyle estilo = estiloEncabesados(libro);
            XSSFCellStyle estiloDatos = estiloDatos(libro);
            XSSFCellStyle estiloMoneda = estiloMoneda(libro);

            for (int i = 0; i < columnas.length; i++) {
                Cell celda = fila.createCell(i);
                celda.setCellValue(columnas[i]);
                celda.setCellStyle(estilo);
                hoja.autoSizeColumn(i);
            }
            int coordenadaRow = 1;
            for (Item item : todos) {
                fila = hoja.createRow(coordenadaRow);

                Cell numItem = fila.createCell(0);
                numItem.setCellValue(item.getNumeroItem());
                numItem.setCellStyle(estiloDatos);

                Cell descripcion = fila.createCell(1);
                descripcion.setCellValue(item.getDescripcion());
                descripcion.setCellStyle(estiloDatos);

                Cell unidad = fila.createCell(2);
                unidad.setCellValue(item.getUnidad());
                unidad.setCellStyle(estiloDatos);

                if (item.getCantidad() != null && item.getPrecioUnitario() != null && item.getSubTotal() != null) {
                    Cell cantidad = fila.createCell(3);
                    cantidad.setCellValue(item.getCantidad());
                    cantidad.setCellStyle(estiloDatos);

                    Cell precioUn = fila.createCell(4);
                    precioUn.setCellValue(item.getPrecioUnitario());
                    precioUn.setCellStyle(estiloMoneda);

                    Cell subTotal = fila.createCell(5);
                    subTotal.setCellValue(item.getSubTotal());
                    subTotal.setCellStyle(estiloMoneda);
                }
                coordenadaRow++;
            }
            Row filaTotal = hoja.createRow(hoja.getLastRowNum() + 1);
            Cell celdaText = filaTotal.createCell(4);
            celdaText.setCellValue("Total:");
            Cell total = filaTotal.createCell(5);
            total.setCellStyle(estiloMoneda);
            total.setCellValue("=SUMA(F2:F123)");

            for (int i = 0; i < columnas.length; i++) {
                hoja.autoSizeColumn(i);
            }
            libro.write(stream);
            libro.close();
        }
        return new ByteArrayInputStream(stream.toByteArray());
    }

    public void importarIOP(InputStream archivo) throws IOException {
        try (XSSFWorkbook libro = new XSSFWorkbook(archivo)) {
            Sheet hoja = libro.getSheetAt(0);
            List<Date> fechasEncabesados = new ArrayList<>();

            /*Registro y guardo los Factores*/
            for (Row fila : hoja) {
                Cell celda = fila.getCell(1);
                if (fila.getRowNum() != 0) {
                    String factor = celda.getStringCellValue();
                    iopServicio.crearIOP(factor);
                }
            }
            /*Se crea una lista con las fechas de los indices*/
            Row fechas = hoja.getRow(0);
            for (Cell celda : fechas) {
                if (celda.getColumnIndex() != 0 && celda.getColumnIndex() != 1) {
                    Date fecha = celda.getDateCellValue();
                    fechasEncabesados.add(fecha);
                }
            }
            /*Se crea una lista con los valores mensuales de los indices, se les asigna a sus factores con las fechas y se guarda en BDD*/
            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row filaActual = hoja.getRow(i);
                List<Double> listaDeValores = new ArrayList<>();
                for (Cell celda : filaActual) {
                    if (celda.getColumnIndex() != 0 && celda.getColumnIndex() != 1) {
                        Double valor = celda.getNumericCellValue();
                        listaDeValores.add(valor);
                    }
                }
                iopServicio.agregarPorExel(i, fechasEncabesados, listaDeValores);
            }
            libro.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ByteArrayInputStream exportIOP() throws IOException {
        try (XSSFWorkbook libro = new XSSFWorkbook(); ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            XSSFCellStyle estiloFechas = estiloEncabesados(libro);
            Sheet hoja = libro.createSheet("IOP-Cba");
            
            /*Se ponen los titulos*/
            Row fila = hoja.createRow(0);
            fila.createCell(0).setCellValue("Orden");
            fila.createCell(1).setCellValue("Factor");

            /*Traigo todos los indices y factores*/
            List<IOP> tabla = iopServicio.todosLosIndices();

            /*Le doy todas las fechas registradas al excel*/
            List<IndiceMensual> fechasIndices = tabla.get(0).getFechas();
            Collections.sort(fechasIndices);
            estiloFechas.setDataFormat(libro.createDataFormat().getFormat("MM-yyyy"));
            for (int i = 0; i < fechasIndices.size(); i++) {
                Cell fechaTitulo = fila.createCell(i + 2);
                fechaTitulo.setCellValue(fechasIndices.get(i).getFecha());
                fechaTitulo.setCellStyle(estiloFechas);
            }
            /*Traigo el orden y el nombre del factor y Le asigno los valores de los indidces a la tabla*/
            int inex = 1;
            for (IOP iop : tabla) {
                fila = hoja.createRow(inex);
                fila.createCell(0).setCellValue(iop.getId());
                fila.createCell(1).setCellValue(iop.getNombreFactor());
                fechasIndices = iop.getFechas();
                Collections.sort(fechasIndices);
                for (int i = 0; i < fechasIndices.size(); i++) {
                    fila.createCell(i + 2).setCellValue(fechasIndices.get(i).getValor());
                }
                inex++;
            }
            libro.write(stream);
            libro.close();
            return new ByteArrayInputStream(stream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private XSSFCellStyle estiloEncabesados(XSSFWorkbook libro) {
        XSSFCellStyle estilo = libro.createCellStyle();
        estilo.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setBorderBottom(BorderStyle.THICK);
        estilo.setBorderTop(BorderStyle.THICK);
        estilo.setBorderLeft(BorderStyle.THICK);
        estilo.setBorderRight(BorderStyle.THICK);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        return estilo;
    }

    private XSSFCellStyle estiloDatos(XSSFWorkbook libro) {
        XSSFCellStyle estilo = libro.createCellStyle();
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        return estilo;
    }

    private XSSFCellStyle estiloMoneda(XSSFWorkbook libro) {
        XSSFCellStyle estilo = libro.createCellStyle();
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        estilo.setDataFormat(libro.createDataFormat().getFormat("$#,##0_);($#,##0)"));
        return estilo;

    }
}