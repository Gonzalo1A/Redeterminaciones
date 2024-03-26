package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.repositorios.ItemRepositorio;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

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
        if (cantidad != null || precioUnitario != null) {
        item.setCantidad(cantidad);
        item.setPrecioUnitario(precioUnitario);
        item.setSubTotal(cantidad * precioUnitario);
       }

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

}
