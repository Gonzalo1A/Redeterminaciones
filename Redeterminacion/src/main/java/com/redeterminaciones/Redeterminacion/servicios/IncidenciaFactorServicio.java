package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.EstilosDeExel;
import com.redeterminaciones.Redeterminacion.entidades.IncidenciaFactor;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.repositorios.IncidenciaFactorRepositorio;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncidenciaFactorServicio {

    @Autowired
    private IncidenciaFactorRepositorio incidenciaFactorRepositorio;
    @Autowired
    private ItemServicio itemServi;


    private final Pattern patron = Pattern.compile("(\\d+[,.]\\d+)\\s*?[Xx*.]\\s*?[Ff](\\d+)");


    @Transactional
    public IncidenciaFactor crearIncidenciaFactor(int factorReferencia, float porcentaje) {
        IncidenciaFactor incidenciaFactor = new IncidenciaFactor();
        incidenciaFactor.setIndice(factorReferencia);
        incidenciaFactor.setPorcentajeIncidencia(porcentaje);
        incidenciaFactorRepositorio.save(incidenciaFactor);
        return incidenciaFactor;
    }

    @Transactional
    public List<IncidenciaFactor> formatearValores(String cadena) {
        List<IncidenciaFactor> listIncidencia = new ArrayList<>();
        Matcher matcher = patron.matcher(cadena);
        while (matcher.find()) {
            String numeroStr = matcher.group(1);
            String porcentajeStr = numeroStr;
            String fValueStr = matcher.group(2);
            float porcentaje = Float.parseFloat(porcentajeStr);
            int numero = Integer.parseInt(fValueStr);
            IncidenciaFactor incFac = crearIncidenciaFactor(numero, porcentaje);
            listIncidencia.add(incFac);
        }
        return listIncidencia;

    }


//    public List<ValoresIncidenciaLista> listaPreparada(Map<String, Object> datosIncidencia) {
//        List<Map<String, Object>> listaIncidenciasMap = (List<Map<String, Object>>) datosIncidencia.get("listaIncidancias");
//        List<ValoresIncidenciaLista> listaIncidencias = new ArrayList<>();
//
//        for (Map<String, Object> incidenciaMap : listaIncidenciasMap) {
//            ValoresIncidenciaLista incidencia = new ValoresIncidenciaLista();
//            incidencia.setIncidencia(incidenciaMap.get("incidencia"));
//            listaIncidencias.add(incidencia);
//        }
//    }
//    public void modificarIncidenciaFactor(Float porcentaje, IOP factorReferencia) {
//        IncidenciaFactor incidenciaFactor = new IncidenciaFactor();
//        incidenciaFactor.setIndice(factorReferencia);
//        incidenciaFactor.setPorcentajeIncidencia(porcentaje);
//        incidenciaFactorRepositorio.save(incidenciaFactor);
//    }

    public IncidenciaFactor buscarIncidenciaFactor(String id) {
        return incidenciaFactorRepositorio.getReferenceById(id);
    }

    public void eliminarIncidenteFactor(String id) {
        incidenciaFactorRepositorio.deleteById(id);
    }

    public ByteArrayInputStream exportarLaIncidenciaDeFactoresExcel(Obra obra) throws Exception {
        String[] columnas = {"ID", "Nro", "DESCRIPCION", "UNIDAD", "CANTIDAD", "PRECIO UNITARIO", "PRECIO", "%INC", "FACTORES"};
        List<Item> todos = obra.getItems();
        ByteArrayOutputStream stream;
        try (XSSFWorkbook libro = new XSSFWorkbook()) {
            stream = new ByteArrayOutputStream();
            Sheet hoja = libro.createSheet("Ingreso de inc de Factores");
            Row fila = hoja.createRow(0);
            XSSFCellStyle estiloDatos = EstilosDeExel.estiloDatos(libro);
            XSSFCellStyle estiloMoneda = EstilosDeExel.estiloMoneda(libro);

            for (int i = 0; i < columnas.length; i++) {
                Cell celda = fila.createCell(i);
                celda.setCellValue(columnas[i]);
                celda.setCellStyle(EstilosDeExel.estiloEncabesados(libro));
                hoja.autoSizeColumn(i);
            }
            int coordenadaRow = 1;
            for (Item item : todos) {
                fila = hoja.createRow(coordenadaRow);

                Cell id = fila.createCell(0);
                id.setCellValue(item.getId());

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

                    Cell incidencia = fila.createCell(7);
                    incidencia.setCellValue(item.getIncidenciaItem());
                    incidencia.setCellStyle(estiloDatos);

                    String cadendaDeFactores = "";
                    List<IncidenciaFactor> factores = item.getIncidenciaFactores();
                    if (!factores.isEmpty()) {
                        for (int i = 0; i < factores.size(); i++) {
                            IncidenciaFactor factor = factores.get(i);
                            cadendaDeFactores += factor.getPorcentajeIncidencia().toString() + "xF" + factor.getIndice();
                            if (i != factores.size() - 1) {
                                cadendaDeFactores += " + ";
                            }
                        }
                        Cell incFactores = fila.createCell(8);
                        incFactores.setCellValue(cadendaDeFactores);
                        incFactores.setCellStyle(estiloDatos);
                    }
                } else {
                    CellRangeAddress rango = new CellRangeAddress(coordenadaRow, coordenadaRow, 3, 8);
                    for (int i = rango.getFirstColumn(); i <= rango.getLastColumn(); i++) {
                        Cell celdaRango = fila.createCell(i);
                        celdaRango.setCellStyle(estiloDatos);
                    }
                    hoja.addMergedRegion(rango);
                }
                coordenadaRow++;
            }
            fila = hoja.createRow(coordenadaRow);
            Cell celdaText = fila.createCell(5);
            celdaText.setCellValue("Total:");
            celdaText.setCellStyle(estiloDatos);
            Cell total = fila.createCell(6);
            total.setCellValue(Double.parseDouble(obra.getTotal()));
            total.setCellStyle(estiloMoneda);
            for (int i = 0; i < columnas.length; i++) {
                hoja.autoSizeColumn(i);
            }
            libro.write(stream);
            libro.close();

        }
        return new ByteArrayInputStream(stream.toByteArray());
    }

    public void importarFactoresDeItemsPorExcel(InputStream archivo) throws Exception {
        try (XSSFWorkbook libro = new XSSFWorkbook(archivo)) {
            Sheet hoja = libro.getSheetAt(0);
            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);
                String factores = null;
                Long id = null;
                Cell celda = fila.getCell(0);
                if (celda != null && celda.getCellType() == CellType.NUMERIC) {
                    id = (long) celda.getNumericCellValue();
                    celda = fila.getCell(8);
                    if (celda != null && celda.getCellType() == CellType.STRING) {
                        factores = celda.getStringCellValue();
                    }
                }
                System.out.println(id + "       " + factores);
                if (factores != null) {
                    List<IncidenciaFactor> incidencias = formatearValores(factores);
                    itemServi.agregarFactor(id, incidencias);
                }
            }
        } catch (Exception e) {
        }
    }

}
