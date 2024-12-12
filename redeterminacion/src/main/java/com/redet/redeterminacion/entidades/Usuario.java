package com.redet.redeterminacion.entidades;

import com.redet.redeterminacion.enumeraciones.Roles;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected String id;
    protected String email;
    protected String password;
    @Enumerated(EnumType.STRING)
    protected Roles rol;


}
