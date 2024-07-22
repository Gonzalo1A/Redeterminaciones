package com.redeterminaciones.Redeterminacion.repositorios;

import com.redeterminaciones.Redeterminacion.entidades.ValorMes;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository

public interface ValorMesRepositorio extends JpaRepository<ValorMes, String> {

    @Query("SELECT v.valor FROM ValorMes v JOIN v.iop i WHERE i.id = :iopId AND v.fecha = :fecha")
    public Double buscarValorPorFecha(@Param("iopId") Integer iopId, @Param("fecha") LocalDate fecha);
}
