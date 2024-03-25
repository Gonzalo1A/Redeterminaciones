package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.ComputoYPresupuesto;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.enumeraciones.Rubros;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.redeterminaciones.Redeterminacion.repositorios.ComputoYPresupuestoRepositorio;

@Service
public class ComputoYPresupuestoServicio {

    @Autowired
    private ComputoYPresupuestoRepositorio computoYPresupuestoRepo;
    @Autowired
    private ObraServicio obServicio;

    @Transactional
    public ComputoYPresupuesto crearComputoYPresupuesto(Rubros rubro, List<Item> items, String nombreObra) {
        ComputoYPresupuesto cyp = new ComputoYPresupuesto();
        cyp.setRubro(rubro);
        cyp.setItems(items);
        Double total = null;
        for (Item item : items) {
            total += item.getSubTotal();
        }
        cyp.setTotal(total);
        computoYPresupuestoRepo.save(cyp);
        Obra obraActual = obServicio.buscarPorNombre(nombreObra);
        obServicio.agregarCyP(obraActual.getId(), cyp);
        return cyp;
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
