package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.ComputoYPresupuesto;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.enumeraciones.Rubros;
import com.redeterminaciones.Redeterminacion.repositorios.ComputoYPresupuestoRepo;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComputoYPresupuestoServicio {

    @Autowired
    private ComputoYPresupuestoRepo computoYPresupuestoRepo;

    @Transactional
    public void crearComputoYPresupuesto(Rubros rubro, List<Item> items) {
        ComputoYPresupuesto nuevoComputo = new ComputoYPresupuesto();
        nuevoComputo.setRubro(rubro);
        nuevoComputo.setItems(items);
        Double total = null;
        for (Item item : items) {
            total += item.getSubTotal();
        }
        nuevoComputo.setTotal(total);
        computoYPresupuestoRepo.save(nuevoComputo);
    }

    @Transactional
    public void moficarComputo(String idComputo, Rubros rubro, Double total, List<Item> items) {
        ComputoYPresupuesto ModificarComputo = computoYPresupuestoRepo.getOne(idComputo);
        ModificarComputo.setRubro(rubro);
        ModificarComputo.setTotal(total);
        ModificarComputo.setItems(items);
        computoYPresupuestoRepo.save(ModificarComputo);
    }

    public ComputoYPresupuesto buscarPorId(String id) {
        return computoYPresupuestoRepo.getById(id);
    }

    @Transactional
    public void eliminarComputo(String id) {
        ComputoYPresupuesto eliminar = buscarPorId(id);
        computoYPresupuestoRepo.delete(eliminar);
    }
}
