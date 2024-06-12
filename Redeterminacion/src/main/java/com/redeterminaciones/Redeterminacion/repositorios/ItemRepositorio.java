package com.redeterminaciones.Redeterminacion.repositorios;

import com.redeterminaciones.Redeterminacion.entidades.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepositorio extends JpaRepository<Item, Long>{

}
