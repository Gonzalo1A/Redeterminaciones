package com.redeterminaciones.Redeterminacion.servicios;

import com.poiji.bind.Poiji;
import com.poiji.option.PoijiOptions;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
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
    
    public void elImportador(File archivo) throws Exception {
        String numItem = "";
        String descripcion = "";
        String unidad = "";
        Double cantidad = 0.0d;
        Double precioUnitario = 0.0d;
        
        InputStream input = new FileInputStream(archivo);
        XSSFWorkbook libro = new XSSFWorkbook(input);
        
        Sheet hoja = libro.getSheetAt(0);
        
        Iterator<Row> filas = hoja.rowIterator();
        Iterator<Cell> columnas;
        
        Row filaActual;
        Cell columnaActual;

        //int coordenadaCelda = 0;
        while (filas.hasNext()) {
            filaActual = filas.next();
            if (filaActual.getRowNum() != 0) {
                columnas = filaActual.iterator();
                
                boolean bandera = true; // bandera para los grupos de items
                while (columnas.hasNext() || bandera) {
                    columnaActual = columnas.next();
                    switch (columnaActual.getColumnIndex()) {
                        case 0:
                            numItem = columnaActual.getStringCellValue();
                            break;
                        case 1:
                            descripcion = columnaActual.getStringCellValue();
                            break;
                        case 2:
                            if (columnaActual.getStringCellValue() != null) {
                                unidad = columnaActual.getStringCellValue();
                                break;
                            } else {
                                bandera = false;
                                break;
                            }
                        case 3:
                            cantidad = columnaActual.getNumericCellValue();
                            break;
                        case 4:
                            precioUnitario = columnaActual.getNumericCellValue();
                            break;
                        default:
                            break;
                    }// FIN DEL SWITCH
                } // FIN DEL RECORRIDO DE CELDAS
                itemServi.crearItem(numItem, descripcion, unidad, cantidad, precioUnitario);
            }/// FIN CONDICIONAL FILA = 0
        }// FIN DEL RECORRIDO DE FILAS
        
    }
    
    public void elImportador2(File archivo) {
        PoijiOptions opcion = PoijiOptions.PoijiOptionsBuilder.settings().sheetIndex(0).limit(5).build();
        List<Item> items = Poiji.fromExcel(archivo, Item.class,opcion);
        for (Item item : items) {
            itemServi.crearItem(item.getNumeroItem(), item.getDescripcion(), item.getUnidad(), item.getCantidad(), item.getCantidad());
        }
    }
    
    public ByteArrayInputStream elExportador() throws Exception {
        String[] columnas = {"Nro", "Descripcion", "Unidad", "Cantidad", "Precio Unitario", "Sub Total"};
        ByteArrayOutputStream stream;
        try (XSSFWorkbook libro = new XSSFWorkbook()) {
            stream = new ByteArrayOutputStream();
            org.apache.poi.ss.usermodel.Sheet hoja = libro.createSheet("Items");
            Row fila = hoja.createRow(0);
            XSSFCellStyle estilo = estiloEncabesados(libro);
            for (int i = 0; i < columnas.length; i++) {
                Cell celda = fila.createCell(i);
                celda.setCellValue(columnas[i]);
                celda.setCellStyle(estilo);
                hoja.autoSizeColumn(i);
            }
            List<Item> todos = itemServi.getAll();
            int coordenadaRow = 1;
            for (Item item : todos) {
                fila = hoja.createRow(coordenadaRow);
                fila.createCell(0).setCellValue(item.getNumeroItem());
                fila.createCell(1).setCellValue(item.getDescripcion());
                fila.createCell(2).setCellValue(item.getUnidad());
                fila.createCell(3).setCellValue(item.getCantidad());
                fila.createCell(4).setCellValue(item.getPrecioUnitario());
                fila.createCell(5).setCellValue(item.getSubTotal());
                hoja.autoSizeColumn(coordenadaRow - 1);
                coordenadaRow++;
            }
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
        estilo.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setBorderBottom(BorderStyle.THICK);
        estilo.setBorderTop(BorderStyle.THICK);
        estilo.setBorderLeft(BorderStyle.THICK);
        estilo.setBorderRight(BorderStyle.THICK);
        
        return estilo;
    }
}
