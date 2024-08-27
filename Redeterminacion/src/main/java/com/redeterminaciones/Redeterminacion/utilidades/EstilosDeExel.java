package com.redeterminaciones.Redeterminacion.utilidades;

import com.redeterminaciones.Redeterminacion.entidades.Obra;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class EstilosDeExel {

    public static XSSFCellStyle estiloEncabesados(XSSFWorkbook libro) {
        XSSFCellStyle estilo = libro.createCellStyle();
        XSSFFont fuente = libro.createFont();
        fuente.setBold(true);
        fuente.setColor(IndexedColors.BLUE1.getIndex());
        estilo.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setBorderBottom(BorderStyle.THICK);
        estilo.setBorderTop(BorderStyle.THICK);
        estilo.setBorderLeft(BorderStyle.THICK);
        estilo.setBorderRight(BorderStyle.THICK);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setFont(fuente);
        estilo.setDataFormat(libro.createDataFormat().getFormat("MM-yyyy"));
        return estilo;
    }
    
        public static XSSFCellStyle estiloRubros(XSSFWorkbook libro) {
        XSSFCellStyle estilo = libro.createCellStyle();
         estilo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        estilo.setBorderBottom(BorderStyle.THIN);
//        estilo.setBorderTop(BorderStyle.THIN);
//        estilo.setBorderLeft(BorderStyle.THIN);
//        estilo.setBorderRight(BorderStyle.THIN);
        return estilo;
    }

    public static XSSFCellStyle estiloDatos(XSSFWorkbook libro) {
        XSSFCellStyle estilo = libro.createCellStyle();
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        estilo.setDataFormat(libro.createDataFormat().getFormat("0.0000"));
        return estilo;
    }

    public static XSSFCellStyle estiloPorcentual(XSSFWorkbook libro) {
        XSSFCellStyle estilo = libro.createCellStyle();
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        estilo.setDataFormat(libro.createDataFormat().getFormat("0.00%"));
        return estilo;
    }

    public static XSSFCellStyle estiloMoneda(XSSFWorkbook libro) {
        XSSFCellStyle estilo = libro.createCellStyle();
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        estilo.setDataFormat(libro.createDataFormat().getFormat("$#,##.00_);($#,##.00)"));
        return estilo;
    }

    public static XSSFCellStyle estiloBloqueado(XSSFWorkbook libro) {
        XSSFCellStyle estiloBloqueo = libro.createCellStyle();
        estiloBloqueo.setLocked(true);
        return estiloBloqueo;
    }

    public static void insertarMembrete(XSSFWorkbook libro, Obra obra) {
        Sheet hojaMembrete = libro.createSheet("Membrete");
        Row fila = hojaMembrete.createRow(0);
        Cell celda = fila.createCell(0);
    }

}
