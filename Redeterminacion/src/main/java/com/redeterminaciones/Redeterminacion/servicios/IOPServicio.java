package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.utilidades.EstilosDeExel;
import com.redeterminaciones.Redeterminacion.entidades.IOP;
import com.redeterminaciones.Redeterminacion.entidades.ValorMes;
import com.redeterminaciones.Redeterminacion.repositorios.IOPRepositorio;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IOPServicio {

    @Autowired
    private IOPRepositorio iopRepo;
    @Autowired
    private ValorMesServicio fechaSer;

    @Transactional
    public void crearIOP(String nombre) {
        IOP op = iopRepo.buscarObraPorNombre(nombre);
        if (op == null) {
            IOP nueva = new IOP();
            nueva.setNombreFactor(nombre);
            iopRepo.save(nueva);
        }
    }

    @Transactional
    public void agregarPorExel(Integer id, List<LocalDate> fechas, List<Double> valores) {
        IOP factor = iopRepo.getById(id);
        List<ValorMes> tablas = new ArrayList<>();
        for (int i = 0; i < fechas.size(); i++) {
            LocalDate fecha = fechas.get(i);
            Double valor = valores.get(i);
            tablas.add(fechaSer.crear(fecha, valor));
        }
        factor.setFechas(tablas);
        iopRepo.save(factor);
    }

    public String nombreDelFactor(int orden) {
        IOP nombre = iopRepo.getById(orden);
        return nombre.getNombreFactor();
    }

    public IOP buscarFactorPorNombre(String nombreFactor) {
        return iopRepo.buscarObraPorNombre(nombreFactor);
    }

    public IOP buscarIndice(Integer orden) {
        return iopRepo.getById(orden);
    }

    public List<IOP> todosLosIndices() {
        return iopRepo.findAll();
    }

    public IOP buscarIOP(int id) {
        return iopRepo.getReferenceById(id);
    }

    public ByteArrayInputStream exportarIOP() throws IOException {
        try (XSSFWorkbook libro = new XSSFWorkbook(); ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            XSSFCellStyle estiloFechas = EstilosDeExel.estiloEncabesados(libro);
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
            List<IOP> tabla = iopRepo.findAll();

            /*Le doy todas las fechas registradas al excel*/
            List<ValorMes> fechasIndices = tabla.get(0).getFechas();
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
            for (Cell cell : fila) {
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

    public void importarIOP(InputStream archivo) throws IOException {
        try (XSSFWorkbook libro = new XSSFWorkbook(archivo)) {
            Sheet hoja = libro.getSheetAt(0);
            List<LocalDate> fechasEncabesados = new ArrayList<>();

            /*Registro y guardo los Factores*/
            for (Row fila : hoja) {
                Cell celda = fila.getCell(1);
                if (fila.getRowNum() != 0) {
                    String factor = celda.getStringCellValue();
                    crearIOP(factor);
                }
            }
            /*Se crea una lista con las fechas de los indices*/
            Row fechas = hoja.getRow(0);
            for (Cell celda : fechas) {
                if (celda.getColumnIndex() != 0 && celda.getColumnIndex() != 1) {
                    LocalDate fecha = celda.getLocalDateTimeCellValue().toLocalDate();
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
                agregarPorExel(i, fechasEncabesados, listaDeValores);
            }
            libro.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
