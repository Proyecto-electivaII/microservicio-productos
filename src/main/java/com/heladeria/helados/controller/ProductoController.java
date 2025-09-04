package com.heladeria.helados.controller;

import com.heladeria.helados.entity.Producto;
import com.heladeria.helados.entity.Inventario;
import com.heladeria.helados.service.ProductoService;
import com.heladeria.helados.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "API para la gestión de productos y su inventario")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private InventarioService inventarioService;



    // ----- Productos -----
    @GetMapping("/listarProductos")
    @Operation(summary = "Obtener todos los productos", description = "Devuelve una lista de todos los productos registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> lista = productoService.listar();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }



    @Operation(summary = "Crea un producto", description = "Crea un nuevo producto")
    @PostMapping("/crear")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Producto> crearProducto(@RequestBody @Parameter(description = "Datos del producto a crear") Producto producto) {
        Producto productoNuevo = productoService.guardar(producto);
        return new ResponseEntity<>(productoNuevo, HttpStatus.CREATED);
    }




    @Operation(summary = "Actualizar un producto", description = "Actualiza los datos de un producto existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado con éxito"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable @Parameter(description = "ID del producto") Integer id,
                                                       @RequestBody @Parameter(description = "Datos actualizados del producto") Producto producto) {
        Producto existente = productoService.buscarPorId(id);
        // si no existe se actualiza
        if (existente != null) {
            existente.setNombre(producto.getNombre());
            existente.setPrecio(producto.getPrecio());
            Producto productoActualizado = productoService.guardar(existente);
            return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto", description = "Elimina un producto basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Producto> eliminarProducto(@PathVariable Integer id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }




    // ----- Inventario -----
    @Operation(summary = "Listar inventario", description = "Devuelve una lista con todos los registros de inventario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inventario obtenida con éxito"),
            @ApiResponse(responseCode = "204", description = "No hay registros de inventario"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/inventario/listarInventario")
    public ResponseEntity<List<Inventario>> listarInventario() {
        List<Inventario> lista = inventarioService.listar();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }




    @Operation(summary = "actualizar inventario", description = "Actualiza la cantidad existente de helados en el inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario actualizado con éxito"),
            @ApiResponse(responseCode = "404", description = "El registro de inventario no fue encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/actualizar-inventario/{id}")
    public ResponseEntity<Producto> actualizarStock(@PathVariable Integer id, @RequestParam Integer cantidad) {
        inventarioService.actualizarStock(id, cantidad);
        return ResponseEntity.noContent().build();
    }
}


