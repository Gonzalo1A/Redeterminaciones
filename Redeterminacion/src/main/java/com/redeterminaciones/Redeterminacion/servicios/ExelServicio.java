package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.Item;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExelServicio {
    
    @Autowired
    private ItemServicio itemServi;
    
    public void elImportador(InputStream archivo) throws Exception {
        XSSFWorkbook libro = new XSSFWorkbook(archivo); // Creamos un Libro a partir del exel
        Sheet hoja = libro.getSheetAt(0);// Se seleccional la Hoja a que contiene los datos a traves de coordenada "quisas a futuro por nombre"
        /*Comenso a recorrer las Filas de la Hoja*/
        for (int i = 1; i <= hoja.getLastRowNum(); i++) {
            /*Se crea una Fila para tratarlas por separado*/
            Row filaActual = hoja.getRow(i);
            if (filaActual != null) {
                String numItem = "";
                String descripcion = "";
                String unidad = "";
                Double cantidad = null;
                Double precioUnitario = null;
                /*Tomo las celdas correspondientes para cada atributo del Item*/
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
                itemServi.crearItem(numItem, descripcion, unidad, cantidad, precioUnitario);
            }
        }
        archivo.close();
        libro.close();
    }
    
    public ByteArrayInputStream elExportador() throws Exception {
        /*Creo un arreglo con los titulares de la tabla*/
        String[] columnas = {"Nro", "Descripcion", "Unidad", "Cantidad", "Precio Unitario", "Sub Total"};
        ByteArrayOutputStream stream;
        try (XSSFWorkbook libro = new XSSFWorkbook()) {
            stream = new ByteArrayOutputStream();
            Sheet hoja = libro.createSheet("Items");
            Row fila = hoja.createRow(0);
            XSSFCellStyle estilo = estiloEncabesados(libro);
            XSSFCellStyle estiloDatos = estiloDatos(libro);
            XSSFCellStyle estiloPrecio = estiloPrecios(libro);
            
            for (int i = 0; i < columnas.length; i++) {
                Cell celda = fila.createCell(i);
                celda.setCellValue(columnas[i]);
                celda.setCellStyle(estilo);
            }
            /*Obtengo la lista de items que van en el Exel*/
            List<Item> todos = itemServi.getAll();
            int coordenadaRow = 1;
            for (Item item : todos) {
                fila = hoja.createRow(coordenadaRow);
                Cell numeroItem = fila.createCell(0);
                numeroItem.setCellValue(item.getNumeroItem());
                numeroItem.setCellStyle(estiloDatos);
                
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
                    
                    Cell precioUnitario = fila.createCell(4);
                    precioUnitario.setCellValue(item.getPrecioUnitario());
                    precioUnitario.setCellStyle(estiloPrecio);
                    
                    Cell subTotal = fila.createCell(5);
                    subTotal.setCellValue(item.getSubTotal());
                    subTotal.setCellStyle(estiloPrecio);
                }
                coordenadaRow++;
            }
            Row total = hoja.createRow(hoja.getLastRowNum() + 1);
            Cell texto = total.createCell(4);
            texto.setCellValue("Total:");
            texto.setCellStyle(estiloDatos);
            for (int i = 0; i < columnas.length; i++) {
                hoja.autoSizeColumn(i);
            }
            libro.write(stream);
            libro.close();
        }
        return new ByteArrayInputStream(stream.toByteArray());
    }
    
    private XSSFCellStyle estiloEncabesados(XSSFWorkbook libro) {
        XSSFCellStyle estilo = libro.createCellStyle();
        /*Se le aplica color al fondo*/
        estilo.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        /*El bordeado de las celdas*/
        estilo.setBorderBottom(BorderStyle.THICK);
        estilo.setBorderTop(BorderStyle.THICK);
        estilo.setBorderLeft(BorderStyle.THICK);
        estilo.setBorderRight(BorderStyle.THICK);
        /*Alineo el contenido de la celda*/
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
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
    
    private XSSFCellStyle estiloPrecios(XSSFWorkbook libro) {
        XSSFCellStyle estilo = libro.createCellStyle();
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        estilo.setDataFormat(libro.createDataFormat().getFormat("$#,##0_);($#,##0)"));
        return estilo;
    }
}
