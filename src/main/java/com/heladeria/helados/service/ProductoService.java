package com.heladeria.helados.service;

import com.heladeria.helados.entity.Producto;
import com.heladeria.helados.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }


    public Optional<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombre(nombre);
    }

    public List<Producto> buscarPorPrecio(BigDecimal precio, String condicion) {
        return switch (condicion.toLowerCase()) {
            case "menor" -> productoRepository.findByPrecioLessThanEqual(precio);
            case "mayor" -> productoRepository.findByPrecioGreaterThanEqual(precio);
            case "igual" -> productoRepository.buscarPorPrecio(precio);
            default -> throw new IllegalArgumentException("Condición inválida: " + condicion);
        };
    }
}
