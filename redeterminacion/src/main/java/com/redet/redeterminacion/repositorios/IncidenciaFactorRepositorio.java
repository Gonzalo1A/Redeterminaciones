package com.redet.redeterminacion.repositorios;

import com.redet.redeterminacion.entidades.IncidenciaFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidenciaFactorRepositorio extends JpaRepository<IncidenciaFactor, String>{

}
