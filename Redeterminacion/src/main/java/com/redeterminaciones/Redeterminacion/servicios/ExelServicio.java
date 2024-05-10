package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.IOP;
import com.redeterminaciones.Redeterminacion.entidades.IncidenciaFactor;
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
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
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
                }

                Cell celdaPrecioUnitario = filaActual.getCell(4);
                if (celdaPrecioUnitario != null && celdaPrecioUnitario.getCellType() == CellType.NUMERIC) {
                    precioUnitario = celdaPrecioUnitario.getNumericCellValue();
                }
                if (!"".equals(numItem) && !"".equals(descripcion)) {
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
            total.setCellValue(Double.parseDouble(obraServicio.buscarPorNombre(nombreObra).getTotal()));

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
            Cell celda = fila.createCell(0);
            celda.setCellValue("Orden");
            celda.setCellStyle(estiloFechas);
            celda = fila.createCell(1);
            celda.setCellValue("Factor");
            celda.setCellStyle(estiloFechas);

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
            Row autoS = hoja.getRow(0);
            for (Cell cell : autoS) {
                hoja.autoSizeColumn(cell.getColumnIndex());
            }
            libro.write(stream);
            libro.close();
            return new ByteArrayInputStream(stream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public ByteArrayInputStream polinomica(String nombreObra) throws IOException {
        try (XSSFWorkbook libro = new XSSFWorkbook(); ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            Sheet hoja = libro.createSheet("Polinomica");
            Row fila = hoja.createRow(0);
            /*Nnum= Ordende Factor / Factor= Nombre del factor / Ponderador = (% de inc factor * % inc Item) / Monto = sum(% de inc factor * subtotal)*/
            XSSFCellStyle estiloTitulo = estiloEncabesados(libro);
            Cell celdaTitular = fila.createCell(0);
            celdaTitular.setCellValue("Num");
            celdaTitular.setCellStyle(estiloTitulo);
            celdaTitular = fila.createCell(1);
            celdaTitular.setCellValue("Factor");
            celdaTitular.setCellStyle(estiloTitulo);
            celdaTitular = fila.createCell(2);
            celdaTitular.setCellValue("Ponderador");
            celdaTitular.setCellStyle(estiloTitulo);
            celdaTitular = fila.createCell(3);
            celdaTitular.setCellValue("Monto Total");
            celdaTitular.setCellStyle(estiloTitulo);
            XSSFCellStyle estiloDatos = estiloDatos(libro);
            XSSFCellStyle estiloPorcentaje = estiloPorcentual(libro);
            XSSFCellStyle estiloMonto = estiloMoneda(libro);
            XSSFCellStyle estiloOrden = estiloDatos(libro);
            estiloOrden.setDataFormat(libro.createDataFormat().getFormat("0"));
            estiloOrden.setAlignment(HorizontalAlignment.CENTER);
            IncidenciaFactor factor1 = new IncidenciaFactor(1, 0.4f);
            IncidenciaFactor factor2 = new IncidenciaFactor(2, 0.2f);
            IncidenciaFactor factor3 = new IncidenciaFactor(3, 0.4f);

            IncidenciaFactor factor11 = new IncidenciaFactor(11, 0.5f);
            IncidenciaFactor factor13 = new IncidenciaFactor(13, 0.5f);

            IncidenciaFactor factor14 = new IncidenciaFactor(2, 0.2f);
            IncidenciaFactor factor15 = new IncidenciaFactor(3, 0.5f);
            IncidenciaFactor factor20 = new IncidenciaFactor(20, 0.2f);
            IncidenciaFactor factor21 = new IncidenciaFactor(21, 0.1f);

            IncidenciaFactor factor33 = new IncidenciaFactor(1, 0.4f);
            IncidenciaFactor factor45 = new IncidenciaFactor(45, 0.6f);

            List<IncidenciaFactor> incFax = new ArrayList<>();
            List<IncidenciaFactor> incFax2 = new ArrayList<>();
            List<IncidenciaFactor> incFax3 = new ArrayList<>();
            List<IncidenciaFactor> incFax4 = new ArrayList<>();

            incFax.add(factor1);
            incFax.add(factor2);
            incFax.add(factor3);
            incFax2.add(factor11);
            incFax2.add(factor13);
            incFax3.add(factor14);
            incFax3.add(factor15);
            incFax3.add(factor20);
            incFax3.add(factor21);
            incFax4.add(factor33);
            incFax4.add(factor45);

            /*Traigo todos los items de la obra*/
            itemServi.calularIncidenciaItem(obraServicio.buscarPorNombre(nombreObra));
            List<Item> items = obraServicio.buscarPorNombre(nombreObra).getItems();
            List<IOP> indices = iopServicio.todosLosIndices();
            items.get(1).setIncidenciaFactores(incFax);
            items.get(2).setIncidenciaFactores(incFax4);
            items.get(3).setIncidenciaFactores(incFax2);
            items.get(4).setIncidenciaFactores(incFax3);
            items.get(5).setIncidenciaFactores(incFax3);
            items.get(6).setIncidenciaFactores(incFax3);
            items.get(7).setIncidenciaFactores(incFax3);
            items.get(8).setIncidenciaFactores(incFax3);
            items.get(9).setIncidenciaFactores(incFax3);
            int coordenadaFila = 1;
            double sumaDelPorc = 0;
            for (IOP indice : indices) {
                fila = hoja.createRow(coordenadaFila);
                int orden = indice.getId();
                String factorNombre = indice.getNombreFactor();
                double ponderador = 0;
                double ponderadorTotal = 0;
                double montoTotalFactor = 0;
                for (Item item : items) {
                    List<IncidenciaFactor> factores = item.getIncidenciaFactores();
                    for (IncidenciaFactor incFactor : factores) {
                        if (incFactor.getIndice() == orden && item.getIncidenciaFactores() != null) {
                            ponderador = item.getIncidenciaItem() * incFactor.getPorcentajeIncidencia();
                            ponderadorTotal = ponderadorTotal + ponderador;
                            montoTotalFactor = montoTotalFactor + (incFactor.getPorcentajeIncidencia() * item.getSubTotal());
                            break;
                        }
                    }
                }
                if (ponderador != 0) {
                    /*Orden*/
                    Cell celda = fila.createCell(0);
                    celda.setCellValue(orden);
                    celda.setCellStyle(estiloOrden);
                    /*Factor*/
                    celda = fila.createCell(1);
                    celda.setCellValue(factorNombre);
                    celda.setCellStyle(estiloDatos);
                    /*Ponderador*/
                    celda = fila.createCell(2);
                    celda.setCellValue(ponderadorTotal);
                    celda.setCellStyle(estiloPorcentaje);
                    /*Monto Total*/
                    celda = fila.createCell(3);
                    celda.setCellValue(montoTotalFactor);
                    celda.setCellStyle(estiloMonto);
                    /*Muevo la coordenada de la fila a la siguiente*/
                    sumaDelPorc += ponderadorTotal;
                    coordenadaFila++;
                }
            }
            fila = hoja.createRow(coordenadaFila);
            celdaTitular = fila.createCell(2);
            celdaTitular.setCellValue(sumaDelPorc);
            celdaTitular.setCellStyle(estiloPorcentaje);
            celdaTitular = fila.createCell(1);
            celdaTitular.setCellValue("TOTALES");
            celdaTitular.setCellStyle(estiloTitulo);
            celdaTitular = fila.createCell(3);
            celdaTitular.setCellValue(Double.parseDouble(obraServicio.buscarPorNombre(nombreObra).getTotal()));
            celdaTitular.setCellStyle(estiloMonto);
            for (int i = 1; i < 3; i++) {
                hoja.autoSizeColumn(i);
            }
            libro.write(stream);
            libro.close();
            return new ByteArrayInputStream(stream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

    }

    public ByteArrayInputStream excelDeEstructutaDeCosto(String nombreObra) throws Exception {
        String[] columnas = {"Nro", "DESCRIPCION", "UNIDAD", "CANTIDAD", "PRECIO UNITARIO", "PRECIO", "%INC", "FACTORES"};
        ByteArrayOutputStream stream;
        List<Item> todos = obraServicio.buscarPorNombre(nombreObra).getItems();

        try (XSSFWorkbook libro = new XSSFWorkbook()) {
            stream = new ByteArrayOutputStream();
            Sheet hoja = libro.createSheet("Estructuras de Costo");
            Row fila = hoja.createRow(0);

            XSSFCellStyle estiloDatos = estiloDatos(libro);
            XSSFCellStyle estiloMoneda = estiloMoneda(libro);

            IncidenciaFactor factor1 = new IncidenciaFactor(1, 0.4f);
            IncidenciaFactor factor2 = new IncidenciaFactor(2, 0.2f);
            IncidenciaFactor factor3 = new IncidenciaFactor(3, 0.4f);

            IncidenciaFactor factor11 = new IncidenciaFactor(11, 0.5f);
            IncidenciaFactor factor13 = new IncidenciaFactor(13, 0.5f);

            IncidenciaFactor factor14 = new IncidenciaFactor(2, 0.2f);
            IncidenciaFactor factor15 = new IncidenciaFactor(3, 0.5f);
            IncidenciaFactor factor20 = new IncidenciaFactor(20, 0.2f);
            IncidenciaFactor factor21 = new IncidenciaFactor(21, 0.1f);

            IncidenciaFactor factor33 = new IncidenciaFactor(1, 0.4f);
            IncidenciaFactor factor45 = new IncidenciaFactor(45, 0.6f);

            List<IncidenciaFactor> incFax = new ArrayList<>();
            List<IncidenciaFactor> incFax2 = new ArrayList<>();
            List<IncidenciaFactor> incFax3 = new ArrayList<>();
            List<IncidenciaFactor> incFax4 = new ArrayList<>();
            incFax.add(factor1);
            incFax.add(factor2);
            incFax.add(factor3);
            incFax2.add(factor11);
            incFax2.add(factor13);
            incFax3.add(factor14);
            incFax3.add(factor15);
            incFax3.add(factor20);
            incFax3.add(factor21);
            incFax4.add(factor33);
            incFax4.add(factor45);
            todos.get(1).setIncidenciaFactores(incFax);
            todos.get(2).setIncidenciaFactores(incFax4);
            todos.get(3).setIncidenciaFactores(incFax2);
            todos.get(4).setIncidenciaFactores(incFax3);
            todos.get(5).setIncidenciaFactores(incFax3);
            todos.get(6).setIncidenciaFactores(incFax3);
            todos.get(7).setIncidenciaFactores(incFax3);
            todos.get(8).setIncidenciaFactores(incFax3);
            todos.get(9).setIncidenciaFactores(incFax3);

            for (int i = 0; i < columnas.length; i++) {
                Cell celda = fila.createCell(i);
                celda.setCellValue(columnas[i]);
                celda.setCellStyle(estiloEncabesados(libro));
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
                switch (item.getUnidad().toUpperCase()) {
                    case "GLOBAL" -> unidad.setCellValue("GLB");
                    case "METROS" -> unidad.setCellValue("m");
                    case "METROS CUADRADOS" -> unidad.setCellValue("m2");
                    case "METROS CUBICOS" -> unidad.setCellValue("m3");
                    case "UNIDAD" -> unidad.setCellValue("UN");
                    default -> unidad.setCellValue(item.getUnidad());
                }
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
                    if (!factores.isEmpty()) {
                        for (int i = 0; i < factores.size(); i++) {
                            IncidenciaFactor factor = factores.get(i);
                            cadendaDeFactores += factor.getPorcentajeIncidencia().toString() + "xF" + factor.getIndice();
                            if (i != factores.size() - 1) {
                                cadendaDeFactores += " + ";
                            }
                        }
                        Cell incFactores = fila.createCell(7);
                        incFactores.setCellValue(cadendaDeFactores);
                        incFactores.setCellStyle(estiloDatos);
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
            libro.write(stream);
            libro.close();
        }
        return new ByteArrayInputStream(stream.toByteArray());
    }

    private XSSFCellStyle estiloEncabesados(XSSFWorkbook libro) {
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
        return estilo;
    }

    private XSSFCellStyle estiloDatos(XSSFWorkbook libro) {
        XSSFCellStyle estilo = libro.createCellStyle();
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        estilo.setDataFormat(libro.createDataFormat().getFormat("0.0000"));
        return estilo;
    }

    private XSSFCellStyle estiloPorcentual(XSSFWorkbook libro) {
        XSSFCellStyle estilo = libro.createCellStyle();
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        estilo.setDataFormat(libro.createDataFormat().getFormat("0.00%"));
        return estilo;
    }

    private XSSFCellStyle estiloMoneda(XSSFWorkbook libro) {
        XSSFCellStyle estilo = libro.createCellStyle();
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        estilo.setDataFormat(libro.createDataFormat().getFormat("$#,##.00_);($#,##.00)"));
        return estilo;
    }
}