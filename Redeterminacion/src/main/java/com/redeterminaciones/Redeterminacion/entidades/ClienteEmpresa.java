package com.redeterminaciones.Redeterminacion.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class ClienteEmpresa extends Usuario {

    private String empresaNombre;
    private String direccion;
    private String numeroCuit;
    @OneToMany
    private List<Obra> obras = new ArrayList<>();

}
