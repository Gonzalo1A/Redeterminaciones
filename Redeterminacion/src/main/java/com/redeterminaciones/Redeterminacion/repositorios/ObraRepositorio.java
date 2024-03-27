package com.redeterminaciones.Redeterminacion.repositorios;

import com.redeterminaciones.Redeterminacion.entidades.Obra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ObraRepositorio extends JpaRepository<Obra, String> {

    @Query("SELECT o FROM Obra o WHERE o.nombre = :nombre")
    public Obra buscarObraPorNombre(@Param("nombre") String nombre);

}
