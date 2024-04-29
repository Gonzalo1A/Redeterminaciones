package com.redeterminaciones.Redeterminacion.repositorios;

import com.redeterminaciones.Redeterminacion.entidades.IOP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IOPRepositorio extends JpaRepository<IOP, Integer> {

    @Query("SELECT i FROM IOP i WHERE i.nombreFactor =:nombreFactor")
    public IOP buscarObraPorNombre(@Param("nombreFactor") String nombreFactor);
    
}
