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
import com.redeterminaciones.Redeterminacion.repositorios.ObraRepositorio;
import java.util.Optional;

@Service
public class ComputoYPresupuestoServicio {

    @Autowired
    private ComputoYPresupuestoRepositorio computoYPresupuestoRepo;
    @Autowired
    private ObraServicio obServicio;
    @Autowired
    private ObraRepositorio obraRepositorio;

    @Transactional
    public ComputoYPresupuesto crearComputoYPresupuesto(Rubros rubro, String id) throws Exception {
        ComputoYPresupuesto cyp = new ComputoYPresupuesto();
        cyp.setRubro(rubro);
        computoYPresupuestoRepo.save(cyp);
        obServicio.agregarCyP(cyp.getId(),id);
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

    @Transactional
    public void agregarItem(List<Item> items, String nombreObra) {
        ComputoYPresupuesto cyp = obServicio.buscarPorNombre(nombreObra).getComputoYPresupuesto();
        cyp.setItems(items);
        computoYPresupuestoRepo.save(cyp);
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
