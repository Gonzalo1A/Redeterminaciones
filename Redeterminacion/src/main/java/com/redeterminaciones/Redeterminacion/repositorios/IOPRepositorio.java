package com.redeterminaciones.Redeterminacion.repositorios;

import com.redeterminaciones.Redeterminacion.entidades.IOP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOPRepositorio extends JpaRepository<IOP, Integer>{
    
    
}
