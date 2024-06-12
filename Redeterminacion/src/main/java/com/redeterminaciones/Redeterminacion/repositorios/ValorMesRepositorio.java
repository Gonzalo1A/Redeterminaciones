package com.redeterminaciones.Redeterminacion.repositorios;

import com.redeterminaciones.Redeterminacion.entidades.ValorMes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ValorMesRepositorio extends JpaRepository<ValorMes, String>{
    
}
