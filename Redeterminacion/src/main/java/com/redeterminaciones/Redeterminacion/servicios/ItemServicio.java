package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.IncidenciaFactor;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.repositorios.ItemRepositorio;
import jakarta.transaction.Transactional;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemServicio {

    @Autowired
    private ItemRepositorio itemRepositorio;

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

}
