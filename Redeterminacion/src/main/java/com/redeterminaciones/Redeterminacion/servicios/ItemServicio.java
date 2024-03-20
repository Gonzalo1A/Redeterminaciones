package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.repositorios.ItemRepositorio;
import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemServicio {

    @Autowired
    private ItemRepositorio itemRepositorio;
    @Autowired
    private ComputoYPresupuestoServicio cypServicio;

    @Transactional
    public void crearItem(String numeroItem, String descripcion, String unidad, Double cantidad, Double precioUnitario) {
        Item item = new Item();

        item.setDescripcion(descripcion);
        item.setNumeroItem(numeroItem);
        item.setUnidad(unidad);
        item.setCantidad(cantidad);
        item.setPrecioUnitario(precioUnitario);
        item.setSubTotal(cantidad * precioUnitario);

        itemRepositorio.save(item);
    }

    public List<Item> listarItems() {
        return itemRepositorio.findAll();
    }

    @Transactional
    public void modificarItem(String idItem, String numeroItem, String descripcion, String unidad, Double cantidad) {
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

    public Item getOne(String id) {
        return itemRepositorio.getOne(id);
    }

    @Transactional
    public void eliminarItem(String id) {
        itemRepositorio.deleteById(id);
    }

    public List<Item> getAll() {
        List<Item> todos = itemRepositorio.findAll();
        return todos;
    }

    public ByteArrayInputStream elExportador() throws Exception {
        String[] columnas = {"N° Item", "Descripcion", "Unidad", "Cantidad", "Precio Unitario", "Sub Total"};
        Workbook workbook = new HSSFWorkbook();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Items");
        Row row = sheet.createRow(0);
        for (int i = 0; i < columnas.length; i++) {
            Cell celda = row.createCell(i);
            celda.setCellValue(columnas[i]);
        }
        List<Item> todos = getAll();
        int coordenadaRow = 1;
        for (Item item : todos) {
            row = sheet.createRow(coordenadaRow);
            row.createCell(0).setCellValue(item.getNumeroItem());
            row.createCell(1).setCellValue(item.getDescripcion());
            row.createCell(2).setCellValue(item.getUnidad());
            row.createCell(3).setCellValue(item.getCantidad());
            row.createCell(4).setCellValue(item.getPrecioUnitario());
            row.createCell(5).setCellValue(item.getSubTotal());
            coordenadaRow++;
        }
        workbook.write(stream);
        workbook.close();
        return new ByteArrayInputStream(stream.toByteArray());
    }
}
