package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.ClienteEmpresa;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
    @Autowired
    private ComputoYPresupuestoServicio cypServicio;

    public List<Item> elImportador(InputStream archivo, ClienteEmpresa clienteEmpresa) throws Exception {
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
                Item item = itemServi.crearItem(numItem, descripcion, unidad, cantidad, precioUnitario);
                items.add(item);
            }
        }
        return items;
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
                if (item.getCantidad() != null && item.getPrecioUnitario() != null && item.getSubTotal() != null) {
                    fila.createCell(3).setCellValue(item.getCantidad());
                    fila.createCell(4).setCellValue(item.getPrecioUnitario());
                    fila.createCell(5).setCellValue(item.getSubTotal());
                }
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
