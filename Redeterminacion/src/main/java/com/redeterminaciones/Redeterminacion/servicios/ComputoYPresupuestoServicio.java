package com.redeterminaciones.Redeterminacion.servicios;

import com.redeterminaciones.Redeterminacion.entidades.ComputoYPresupuesto;
import com.redeterminaciones.Redeterminacion.entidades.Item;
import com.redeterminaciones.Redeterminacion.enumeraciones.Rubros;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.redeterminaciones.Redeterminacion.repositorios.ComputoYPresupuestoRepositorio;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ComputoYPresupuestoServicio {

    @Autowired
    private ComputoYPresupuestoRepositorio computoYPresupuestoRepo;
    @Autowired
    private ObraServicio obServicio;

    @Transactional
    public ComputoYPresupuesto crearComputoYPresupuesto(Rubros rubro, String id) throws Exception {
        ComputoYPresupuesto cyp = new ComputoYPresupuesto();
        cyp.setRubro(rubro);
        computoYPresupuestoRepo.save(cyp);
        obServicio.agregarCyP(cyp.getId(), id);
        return cyp;
    }

    @Transactional
    public void moficarComputo(String idComputo, Rubros rubro, String total, List<Item> items) {
        ComputoYPresupuesto ModificarComputo = computoYPresupuestoRepo.getOne(idComputo);
        ModificarComputo.setRubro(rubro);
        ModificarComputo.setTotal(total);
        ModificarComputo.setItems(items);
        computoYPresupuestoRepo.save(ModificarComputo);
    }

    @Transactional
    public void calcularTotal(String nombreObra) {
        ComputoYPresupuesto cyp = obServicio.buscarPorNombre(nombreObra).getComputoYPresupuesto();
        double total = 0d;
        for (Item item : cyp.getItems()) {
            if (item.getSubTotal() != null) {
                total += item.getSubTotal();
            }
        }
        BigDecimal resultado = new BigDecimal(total);
        cyp.setTotal(resultado.setScale(4,RoundingMode.HALF_UP).toString());
        computoYPresupuestoRepo.save(cyp);
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
