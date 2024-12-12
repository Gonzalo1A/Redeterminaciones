package com.redet.redeterminacion.repositorios;

import com.redet.redeterminacion.entidades.AvanceObraReal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvanceRealRepositorio extends JpaRepository<AvanceObraReal, String> {
    
}
