package com.heladeria.helados.repository;

import com.heladeria.helados.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
    // Busca por la FK producto.id (único por producto)
    Optional<Inventario> findByProducto_Id(Integer productoId);
}
