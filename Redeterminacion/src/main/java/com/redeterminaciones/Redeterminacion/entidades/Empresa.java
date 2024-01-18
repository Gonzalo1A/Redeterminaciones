package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Data;

@Entity
@Data
public class Empresa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    
    private String nombre;
    private Double oferta;
    @OneToMany
    private List<Obra> obras;

}
