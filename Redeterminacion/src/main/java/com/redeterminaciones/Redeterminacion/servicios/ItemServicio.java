package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.AvanceObraReal;
import com.redeterminaciones.Redeterminacion.utilidades.EstilosDeExel;
import com.redeterminaciones.Redeterminacion.entidades.IncidenciaFactor;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.ValorMes;
import com.redeterminaciones.Redeterminacion.repositorios.ItemRepositorio;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
public class ItemServicio {

    @Autowired
    private ItemRepositorio itemRepositorio;
    @Autowired
    private ValorMesServicio valorMesServi;
    @Autowired
    private AvanceRealServicio avanceRealServicio;

    @Transactional
    public Item crearItem(String numeroItem, String descripcion, String unidad, Double cantidad, Double precioUnitario) {
        Item item = new Item();

        item.setDescripcion(descripcion);
        item.setNumeroItem(numeroItem);
        item.setUnidad(unidad);
        if (cantidad != null || precioUnitario != null) {
            item.setCantidad(cantidad);
            item.setPrecioUnitario(precioUnitario);
            Double resultado = cantidad * precioUnitario;
            resultado = (double) (Math.round(resultado * 10000)) / 10000;
            item.setSubTotal(resultado);
            item.setRubro(false);
        } else {
            item.setRubro(true);
        }

        itemRepositorio.save(item);
        return item;
    }

    public List<Item> listarItems() {
        return itemRepositorio.findAll();
    }

    @Transactional
    public void modificarItem(Long idItem, String numeroItem, String descripcion, String unidad, Double cantidad) {
        Optional<Item> respuesta = itemRepositorio.findById(idItem);
        if (respuesta.isPresent()) {
            Item item = respuesta.get();

            item.setDescripcion(descripcion);
            item.setNumeroItem(numeroItem);
            item.setUnidad(unidad);
            item.setCantidad(cantidad);
            itemRepositorio.save(item);
        }
    }

    @Transactional
    public void calularIncidenciaItem(Obra obra) {
        Double total = Double.parseDouble(obra.getTotal());
        for (Item item : obra.getItems()) {
            if (item.getSubTotal() != null) {
                item.setIncidenciaItem(item.getSubTotal() / total);
                itemRepositorio.save(item);
            }
        }
    }

    public List<String> cadenaIncidencias(List<Item> item) {
        List<String> cadenas = new ArrayList<>();
        for (Item items : item) {
            int actual = 0;
            String incidencias = "";
            for (IncidenciaFactor cad : items.getIncidenciaFactores()) {
                int fin = items.getIncidenciaFactores().size() - 1;
                incidencias += cad.getPorcentajeIncidencia().toString() + " X F" + cad.getIndice();
                if (fin != actual) {
                    incidencias += " + ";
                    actual++;
                }
            }
            cadenas.add(incidencias);
        }
        return cadenas;
    }

    @Transactional
    public void agregarFactor(Long idItem, List<IncidenciaFactor> incidencias) {

        Optional<Item> respuesta = itemRepositorio.findById(idItem);

        if (respuesta.isPresent()) {
            Item item = respuesta.get();
            item.setIncidenciaFactores(incidencias);
            itemRepositorio.save(item);
        }
    }

    @Transactional
    public void agregarAvanceReal(Long idItem, List<AvanceObraReal> avanceReal) {
        Optional<Item> respuesta = itemRepositorio.findById(idItem);
        if (respuesta.isPresent()) {
            Item item = respuesta.get();
            item.setAvanceObraReal(avanceReal);
            itemRepositorio.save(item);
        }
    }

    @Transactional
    public void agregarAvanceTeorico(Long idItem, List<ValorMes> avanceTeorico) {
        Optional<Item> respuesta = itemRepositorio.findById(idItem);
        if (respuesta.isPresent()) {
            Item item = respuesta.get();
            item.setAvanceTeorico(avanceTeorico);
            itemRepositorio.save(item);
        }
    }

    public Item getOne(Long id) {
        return itemRepositorio.getOne(id);
    }

    @Transactional
    public void eliminarItem(Long id) {
        itemRepositorio.deleteById(id);
    }

    public List<Item> getAll() {
        List<Item> todos = itemRepositorio.findAll();
        return todos;
    }

    public ByteArrayInputStream exportarModeloParaIngresarItemsPorExcel(Obra obra) throws Exception {
        String[] columnas = {"Nro", "Descripcion", "Unidad", "Cantidad", "Precio Unitario", "Sub Total"};
        ByteArrayOutputStream stream;
        List<Item> todos = obra.getItems();

        try (XSSFWorkbook libro = new XSSFWorkbook()) {
            stream = new ByteArrayOutputStream();
            Sheet hoja = libro.createSheet("Items");
            XSSFCellStyle estilo = EstilosDeExel.estiloEncabesados(libro);
            XSSFCellStyle estiloDatos = EstilosDeExel.estiloDatos(libro);
            XSSFCellStyle estiloMoneda = EstilosDeExel.estiloMoneda(libro);

            Row fila = hoja.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell celda = fila.createCell(i);
                celda.setCellValue(columnas[i]);
                celda.setCellStyle(estilo);
            }
            int coordenadaRow = 1;
            if (!todos.isEmpty()) {
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
                Row filaTotal = hoja.createRow(coordenadaRow);
                Cell celdaText = filaTotal.createCell(4);
                Cell total = filaTotal.createCell(5);
                celdaText.setCellValue("Total:");
                celdaText.setCellStyle(estilo);
                total.setCellStyle(estiloMoneda);
                total.setCellValue(Double.parseDouble(obra.getTotal()));
            }

            for (int i = 0; i < columnas.length; i++) {
                hoja.autoSizeColumn(i);
            }
            libro.write(stream);
            libro.close();
        }
        return new ByteArrayInputStream(stream.toByteArray());
    }

    public List<Item> importarItemsPorExcel(InputStream archivo) throws Exception {
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
                    Item item = crearItem(numItem, descripcion, unidad, cantidad, precioUnitario);
                    items.add(item);
                }
            }
        }
        libro.close();
        return items;
    }

    public ByteArrayInputStream exportarModeloParaAvanceDeObraTeoricoExcel(Obra obra) throws Exception {
        String[] columnas = {"ID", "Nro", "DESCRIPCION", "UNIDAD", "CANTIDAD", "PRECIO UNITARIO", "PRECIO"};
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

            List<Date> mesesDeObra = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            Date fechaInicio = obra.getFechaDeReeplanteo();
            calendar.setTime(fechaInicio);
            calendar.add(Calendar.DAY_OF_MONTH, obra.getDiasPlazoDeObra());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaFinal = calendar.getTime();

            LocalDate inicio = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1).plusMonths(1);
            LocalDate fin = fechaFinal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            YearMonth comienso = YearMonth.from(inicio);
            YearMonth finalisima = YearMonth.from(fin);
            YearMonth mesActual = comienso;

            while (!mesActual.isAfter(finalisima)) {
                LocalDate ultimoDia = mesActual.atEndOfMonth();
                Date convierte = (Date.from(ultimoDia.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                mesesDeObra.add(convierte);
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

    public void importarAvanceDeObraTeoricoPorExcel(InputStream archivo) throws Exception {
        try (XSSFWorkbook libro = new XSSFWorkbook(archivo)) {
            Sheet hoja = libro.getSheetAt(0);
            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                List<ValorMes> avanceTeorico = new ArrayList<>();
                Row filaTitular = hoja.getRow(0);
                Row fila = hoja.getRow(i);
                Long id = null;
                Date fecha;
                Double valor;
                Cell celda = fila.getCell(0);
                if (celda != null) {
                    id = (long) celda.getNumericCellValue();
                    Item separador = getOne(id);
                    if (!separador.isRubro()) {
                        for (int j = 7; j < (int) fila.getLastCellNum(); j++) {
                            celda = fila.getCell(j);
                            if (celda != null && celda.getCellType() == CellType.NUMERIC) {
                                valor = celda.getNumericCellValue();
                                fecha = filaTitular.getCell(j).getDateCellValue();
                                avanceTeorico.add(valorMesServi.crear(fecha, valor));
                            }
                        }
                    }
                }
                if (id != null && !avanceTeorico.isEmpty()) {
                    agregarAvanceTeorico(id, avanceTeorico);
                }
            }
            libro.close();
        }
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
                    Item carga = itemRepositorio.getById(id);
                    agregarAvanceReal(id, avanceRealServicio.cargarAvance(carga, valorMesServi.crear(fecha, valor)));
                }
            }
            libro.close();
        }
    }
}
