package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.EstilosDeExel;
import com.redeterminaciones.Redeterminacion.entidades.IOP;
import com.redeterminaciones.Redeterminacion.entidades.IncidenciaFactor;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExelServicio {

    @Autowired
    private ObraServicio obraServicio;
    @Autowired
    private IOPServicio iopServicio;

    public ByteArrayInputStream excelDeEstructutaDeCosto(String nombreObra) throws Exception {
        String[] columnas = {"Nro", "DESCRIPCION", "UNIDAD", "CANTIDAD", "PRECIO UNITARIO", "PRECIO", "%INC", "FACTORES"};
        ByteArrayOutputStream stream;
        List<Item> todos = obraServicio.buscarPorNombre(nombreObra).getItems();

        try (XSSFWorkbook libro = new XSSFWorkbook()) {
            stream = new ByteArrayOutputStream();
            Sheet hoja = libro.createSheet("Estructuras de Costo");
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

                    Cell incidencia = fila.createCell(6);
                    incidencia.setCellValue(item.getIncidenciaItem());
                    incidencia.setCellStyle(estiloDatos);

                    String cadendaDeFactores = "";
                    List<IncidenciaFactor> factores = item.getIncidenciaFactores();
                    Cell incFactores = fila.createCell(7);
                    incFactores.setCellStyle(estiloDatos);
                    boolean bandera = false;
                    Float sumatoria = 0f;
                    int ordenRepetido = 0;

                    if (!factores.isEmpty()) {
                        for (int i = 0; i < factores.size(); i++) {
                            IncidenciaFactor factor = factores.get(i);
                            sumatoria += factor.getPorcentajeIncidencia();
                            cadendaDeFactores += factor.getPorcentajeIncidencia().toString() + "xF" + factor.getIndice();
                            if (ordenRepetido == factor.getIndice()) {
                                bandera = true;
                            }
                            ordenRepetido = factor.getIndice();
                            if (i != factores.size() - 1) {
                                cadendaDeFactores += " + ";
                            }
                        }
                        incFactores.setCellValue(cadendaDeFactores);
                        if (sumatoria > 1 || sumatoria < 1 || bandera) {
                            XSSFCellStyle error = EstilosDeExel.estiloDatos(libro);
                            error.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                            error.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                            incFactores.setCellStyle(error);
                        }
                    }
                } else {
                    CellRangeAddress rango = new CellRangeAddress(coordenadaRow, coordenadaRow, 2, 7);
                    for (int i = rango.getFirstColumn(); i <= rango.getLastColumn(); i++) {
                        Cell celdaRango = fila.createCell(i);
                        celdaRango.setCellStyle(estiloDatos);
                    }
                    hoja.addMergedRegion(rango);
                }
                coordenadaRow++;
            }
            fila = hoja.createRow(coordenadaRow);
            Cell celdaText = fila.createCell(4);
            celdaText.setCellValue("Total:");
            celdaText.setCellStyle(estiloDatos);
            Cell total = fila.createCell(5);
            total.setCellValue(Double.parseDouble(obraServicio.buscarPorNombre(nombreObra).getTotal()));
            total.setCellStyle(estiloMoneda);
            for (int i = 0; i < columnas.length; i++) {
                hoja.autoSizeColumn(i);
            }

            Sheet hojaPolinomica = libro.createSheet("Polinomica");
            Row filaPolin = hojaPolinomica.createRow(0);
            /*Nnum= Ordende Factor / Factor= Nombre del factor / Ponderador = (% de inc factor * % inc Item) / Monto = sum(% de inc factor * subtotal)*/

            XSSFCellStyle estiloTitulo = EstilosDeExel.estiloEncabesados(libro);
            Cell celdaTitular = filaPolin.createCell(0);
            celdaTitular.setCellValue("Num");
            celdaTitular.setCellStyle(estiloTitulo);
            celdaTitular = filaPolin.createCell(1);
            celdaTitular.setCellValue("Factor");
            celdaTitular.setCellStyle(estiloTitulo);
            celdaTitular = filaPolin.createCell(2);
            celdaTitular.setCellValue("Ponderador");
            celdaTitular.setCellStyle(estiloTitulo);
            celdaTitular = filaPolin.createCell(3);
            celdaTitular.setCellValue("Monto Total");
            celdaTitular.setCellStyle(estiloTitulo);

            XSSFCellStyle estiloPorcentaje = EstilosDeExel.estiloPorcentual(libro);
            XSSFCellStyle estiloMonto = EstilosDeExel.estiloMoneda(libro);
            XSSFCellStyle estiloOrden = EstilosDeExel.estiloDatos(libro);
            estiloOrden.setDataFormat(libro.createDataFormat().getFormat("0"));
            estiloOrden.setAlignment(HorizontalAlignment.CENTER);
            /*Traigo todos los todos de la obra*/

            List<IOP> indices = iopServicio.todosLosIndices();

            int coordenadaFila = 1;
            double sumaDelPorc = 0;
            for (IOP indice : indices) {
                filaPolin = hojaPolinomica.createRow(coordenadaFila);
                int orden = indice.getId();
                String factorNombre = indice.getNombreFactor();
                double ponderador = 0;
                double ponderadorTotal = 0;
                double montoTotalFactor = 0;
                for (Item item : todos) {
                    List<IncidenciaFactor> factores = item.getIncidenciaFactores();
                    if (!factores.isEmpty()) {
                        for (IncidenciaFactor incFactor : factores) {
                            if (incFactor.getIndice() == orden) {
                                ponderador = item.getIncidenciaItem() * incFactor.getPorcentajeIncidencia();
                                ponderadorTotal = ponderadorTotal + ponderador;
                                montoTotalFactor = montoTotalFactor + (incFactor.getPorcentajeIncidencia() * item.getSubTotal());
                                break;
                            }
                        }
                    }
                }
                if (ponderador != 0) {
                    /*Orden*/
                    Cell celda = filaPolin.createCell(0);
                    celda.setCellValue(orden);
                    celda.setCellStyle(estiloOrden);
                    /*Factor*/
                    celda = filaPolin.createCell(1);
                    celda.setCellValue(factorNombre);
                    celda.setCellStyle(estiloDatos);
                    /*Ponderador*/
                    celda = filaPolin.createCell(2);
                    celda.setCellValue(ponderadorTotal);
                    celda.setCellStyle(estiloPorcentaje);
                    /*Monto Total*/
                    celda = filaPolin.createCell(3);
                    celda.setCellValue(montoTotalFactor);
                    celda.setCellStyle(estiloMonto);
                    /*Muevo la coordenada de la filaPolin a la siguiente*/
                    sumaDelPorc += ponderadorTotal;
                    coordenadaFila++;
                }
            }
            filaPolin = hojaPolinomica.createRow(coordenadaFila);
            celdaTitular = filaPolin.createCell(0);
            celdaTitular.setCellStyle(estiloTitulo);
            celdaTitular = filaPolin.createCell(1);
            celdaTitular.setCellValue("TOTALES");
            celdaTitular.setCellStyle(estiloTitulo);
            CellRangeAddress rango = new CellRangeAddress(coordenadaRow, coordenadaRow, 0, 1);
            hoja.addMergedRegion(rango);

            celdaTitular = filaPolin.createCell(2);
            celdaTitular.setCellValue(sumaDelPorc);
            celdaTitular.setCellStyle(estiloPorcentaje);

            celdaTitular = filaPolin.createCell(3);
            celdaTitular.setCellValue(Double.parseDouble(obraServicio.buscarPorNombre(nombreObra).getTotal()));
            celdaTitular.setCellStyle(estiloMonto);

            libro.write(stream);
            libro.close();
            return new ByteArrayInputStream(stream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
