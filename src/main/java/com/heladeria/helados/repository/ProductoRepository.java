package com.heladeria.helados.repository;

import com.heladeria.helados.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    Optional<Producto> findByNombre(String nombre);

    List<Producto> findByPrecioLessThanEqual(BigDecimal precio);

    List<Producto> findByPrecioGreaterThanEqual(BigDecimal precio);

    @Query("SELECT p FROM Producto p WHERE p.precio = :precio")
    List<Producto> buscarPorPrecio(@Param("precio") BigDecimal precio);


}