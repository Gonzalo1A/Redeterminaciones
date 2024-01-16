package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.ComputoYPresupuesto;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.enumeraciones.Rubros;
import com.redeterminaciones.Redeterminacion.repositorios.ComputoYPresupuestoRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComputoYPresupuestoServicio {

    @Autowired
    private ComputoYPresupuestoRepo computoYPresupuestoRepo;

    public void crearComputoYPresupuesto(Rubros rubro, Double subTotal, Double total, List<Item> items) {
        ComputoYPresupuesto nuevoComputo = new ComputoYPresupuesto();
        nuevoComputo.setRubro(rubro);
        nuevoComputo.setSubTotales(subTotal);
        nuevoComputo.setTotal(total);
        nuevoComputo.setItems(items);
        computoYPresupuestoRepo.save(nuevoComputo);
    }

    public void moficarComputo(String idComputo, Rubros rubro, Double subTotal, Double total, List<Item> items) {
        ComputoYPresupuesto ModificarComputo = computoYPresupuestoRepo.getOne(idComputo);
        ModificarComputo.setRubro(rubro);
        ModificarComputo.setSubTotales(subTotal);
        ModificarComputo.setTotal(total);
        ModificarComputo.setItems(items);
        computoYPresupuestoRepo.save(ModificarComputo);
    }

    public ComputoYPresupuesto buscarPorNombre() {
        return null;
    }
}
