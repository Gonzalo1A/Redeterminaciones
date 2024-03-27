package com.redeterminaciones.Redeterminacion.entidades;

import com.redeterminaciones.Redeterminacion.enumeraciones.Rubros;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Data;

@Data
@Entity
public class ComputoYPresupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Enumerated(EnumType.STRING)
    private Rubros rubro;
    private Double total;
    @OneToMany
    private List<Item> items;
    
}
