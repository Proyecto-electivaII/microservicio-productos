package com.heladeria.helados.controller;
import com.heladeria.helados.entity.Producto;
import com.heladeria.helados.entity.Inventario;
import com.heladeria.helados.service.ProductoService;
import com.heladeria.helados.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private InventarioService inventarioService;

    // ----- Productos -----

    @GetMapping("/productos")
    public List<Producto> listarProductos() {
        return productoService.listar();
    }

    @PostMapping("/productos")
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoService.guardar(producto);
    }

    @PutMapping("/productos/{id}")
    public Producto editarProducto(@PathVariable Integer id, @RequestBody Producto producto) {
        Producto existente = productoService.buscarPorId(id);
        // si no existe se actualiza
        if (existente != null) {
            existente.setNombre(producto.getNombre());
            existente.setPrecio(producto.getPrecio());
            return productoService.guardar(existente);
        }
        return existente;
    }

    @DeleteMapping("/productos/{id}")
    public void eliminarProducto(@PathVariable Integer id) {
        productoService.eliminar(id);
    }

    // ----- Inventario -----
    @GetMapping("/inventario")
    public ResponseEntity<List<Inventario>> listarInventario() {
        return new ResponseEntity<>(inventarioService.listar(), HttpStatus.OK);
    }

    @PatchMapping("/{productoId}")
    public void actualizarStock(
            @PathVariable Integer productoId,
            @RequestParam Integer cantidad) {
        inventarioService.actualizarStock(productoId, cantidad);
    }

    @GetMapping("/hola/{nombre}")
    public ResponseEntity<String> hola(@PathVariable String nombre) {

        if(nombre.equals("valentina")){
            return new  ResponseEntity<>("valentina", HttpStatus.OK);
        }

        return new  ResponseEntity<>("no es valentina", HttpStatus.BAD_REQUEST);
    }
}