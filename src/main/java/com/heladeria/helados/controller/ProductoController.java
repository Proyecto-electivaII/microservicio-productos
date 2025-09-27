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
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
            @ApiResponse(responseCode = "200", description = "Producto eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<String> eliminarProducto(@PathVariable Integer id) {

        Producto existente = productoService.buscarPorId(id);
        if (existente != null) {
            productoService.eliminar(id);
            return new ResponseEntity<>("Producto eliminado", HttpStatus.OK);
        }
        return new ResponseEntity<>("Producto no existe", HttpStatus.NOT_FOUND);
    }




    // ----- Inventario -----
    @Operation(summary = "Listar inventario", description = "Devuelve una lista con todos los registros de inventario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inventario obtenida con éxito"),
            @ApiResponse(responseCode = "404", description = "No hay registros de inventario"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/inventario/listarInventario")
    public ResponseEntity<List<Inventario>> listarInventario() {
        List<Inventario> lista = inventarioService.listar();
        if (lista.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }



    @Operation(summary = "actualizar inventario", description = "Actualiza la cantidad existente de helados en el inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario actualizado con éxito"),
            @ApiResponse(responseCode = "404", description = "El registro de inventario no fue encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/actualizar-inventario/{id}")
    public ResponseEntity<Inventario> actualizarStock(@PathVariable Integer id, @RequestParam Integer cantidad) {
        Inventario inventario = inventarioService.actualizarStock(id, cantidad);
        if(inventario == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(inventario, HttpStatus.OK);
    }

    // ----- Métodos de búsqueda -----

    @GetMapping("/buscarPorId/{id}")
    @Operation(summary = "Buscar producto por ID", description = "Obtiene un producto según su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Producto> buscarPorId(
            @PathVariable @Parameter(description = "ID del producto") Integer id) {
        Producto producto = productoService.buscarPorId(id);
        if (producto != null) {
            return new ResponseEntity<>(producto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @GetMapping("/buscarPorNombre")
    @Operation(summary = "Buscar producto por nombre", description = "Obtiene un producto según su nombre exacto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<?> buscarPorNombre(
            @RequestParam @Parameter(description = "Nombre del producto") String nombre) {
        return productoService.buscarPorNombre(nombre)
                .<ResponseEntity<?>>map(producto -> new ResponseEntity<>(producto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>("Producto no encontrado", HttpStatus.NOT_FOUND));
    }



    @GetMapping("/buscarPorPrecio")
    @Operation(summary = "Buscar producto por precio", description = "Busca productos por precio con una condición (mayor, menor o igual).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos encontrados"),
            @ApiResponse(responseCode = "400", description = "Condición inválida"),
            @ApiResponse(responseCode = "404", description = "No se encontraron productos")
    })
    public ResponseEntity<?> buscarPorPrecio(
            @RequestParam @Parameter(description = "Precio a comparar") BigDecimal precio,
            @RequestParam @Parameter(description = "Condición de comparación (mayor, menor, igual)") String condicion) {
        try {
            List<Producto> productos = productoService.buscarPorPrecio(precio, condicion);
            if (productos.isEmpty()) {
                return new ResponseEntity<>("No se encontraron productos", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}


