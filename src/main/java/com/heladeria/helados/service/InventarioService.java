package com.heladeria.helados.service;

import com.heladeria.helados.entity.Inventario;
import com.heladeria.helados.entity.Producto;
import com.heladeria.helados.repository.InventarioRepository;
import com.heladeria.helados.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.ResourceTransformer;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ResourceTransformer resourceTransformer;

    public List<Inventario> listar() {
        return inventarioRepository.findAll();
    }

    public Inventario actualizarStock(Integer productoId, Integer cantidad) {
        // validar si existe producto
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (!productoOpt.isPresent()) {
            return null;
        }

        Producto producto = productoOpt.get();

        // buscar en inventario
        Optional<Inventario> inventarioOpt = inventarioRepository.findByProducto_Id(productoId);

        if (inventarioOpt.isPresent()) {
            // si existe en inventario -> actualizar stock
            Inventario inventario = inventarioOpt.get();
            inventario.setCantidad(cantidad);
            inventarioRepository.save(inventario);
            return inventario;
        } else {
            // si no existe en inventario -> crear nuevo
            Inventario nuevo = new Inventario();
            nuevo.setProducto(producto);
            nuevo.setCantidad(cantidad);
            inventarioRepository.save(nuevo);
            return nuevo;
        }
    }
}