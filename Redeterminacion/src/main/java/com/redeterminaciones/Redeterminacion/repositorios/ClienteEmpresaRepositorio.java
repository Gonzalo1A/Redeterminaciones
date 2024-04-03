package com.redeterminaciones.Redeterminacion.repositorios;

import com.redeterminaciones.Redeterminacion.entidades.ClienteEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteEmpresaRepositorio extends JpaRepository<ClienteEmpresa, String>{

    @Query("SELECT c FROM ClienteEmpresa c  WHERE c.email = :email")
    public ClienteEmpresa buscarPorEmail(@Param("email") String email);
}
