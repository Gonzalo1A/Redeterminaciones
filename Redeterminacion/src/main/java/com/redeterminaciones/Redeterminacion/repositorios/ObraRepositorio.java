package com.redeterminaciones.Redeterminacion.repositorios;

import com.redeterminaciones.Redeterminacion.entidades.Obra;
import com.redeterminaciones.Redeterminacion.entidades.Redeterminacion;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

@Repository
public interface ObraRepositorio extends JpaRepository<Obra, String> {

    @Query("SELECT o FROM Obra o WHERE o.nombre LIKE %:nombre%")
    public Obra buscarObraPorNombre(@Param("nombre") String nombre);

    @Query("SELECT r.mesSolicitud FROM Obra o JOIN o.redeterminaciones r WHERE o.id = :obraId ORDER BY r.mesSolicitud DESC LIMIT 1")
    public @DateTimeFormat(pattern = "yyyy-MM")
    LocalDate buscarMesUltimaSolicitud(@Param("obraId") String obraId);

    @Query("SELECT r FROM Obra o JOIN o.redeterminaciones r WHERE o.id = :obraId ORDER BY r.mesSolicitud DESC LIMIT 1")
    Redeterminacion buscarUltimaRedet(@Param("obraId") String obraId);

}
