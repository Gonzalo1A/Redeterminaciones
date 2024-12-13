
package com.redet.redeterminacion.repositorios;

import com.redet.redeterminacion.entidades.Redeterminacion;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RedeterminacionRepositorio extends JpaRepository<Redeterminacion, Integer> {
    
    @Query("SELECT r FROM Redeterminacion r WHERE r.mesSolicitud = :mesSolicitud")
    public Redeterminacion buscarRedetPorMesSolicitud(@Param("mesSolicitud") LocalDate nombreFactor);

}
