package dao;

import model.Orden;
import model.Repuesto;
import java.io.*;
import java.util.*;

/**
 *
 * @author gustavo-fuentes
 */
public class RepuestoDAO {
    private Map<String, Repuesto> inventario = new HashMap<>();

    
    public void agregarRepuesto(Repuesto r) {
        if (inventario.containsKey(r.getId())) {
            throw new IllegalArgumentException("Ya existe un repuesto con el ID: " + r.getId());
        }
        inventario.put(r.getId(), r);
    }

    
    public void modificarRepuesto(String id, String nuevoNombre, double nuevoPrecio) throws Exception {
        Repuesto repuesto = inventario.get(id);
        if (repuesto == null) {
            throw new Exception("El repuesto con ID " + id + " no existe.");
        }
        if (nuevoPrecio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        repuesto.setPrecio(nuevoPrecio);
        
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {               
        }
    }

        public synchronized void abastecer(String id, int cantidadIngresada) throws Exception {
        if (cantidadIngresada <= 0) {
            throw new IllegalArgumentException("La cantidad a abastecer debe ser mayor que cero.");
        }
        Repuesto repuesto = inventario.get(id);
        if (repuesto == null) {
            throw new Exception("El repuesto con ID " + id + " no existe.");
        }
        
        repuesto.setStock(repuesto.getStock() + cantidadIngresada);
    }

    
    public List<Repuesto> obtenerExistencias() {
        return new ArrayList<>(inventario.values());
    }

    
    public List<Repuesto> obtenerAgotados() {
        List<Repuesto> agotados = new ArrayList<>();
        for (Repuesto r : inventario.values()) {
            if (r.getStock() == 0) {
                agotados.add(r);
            }
        }
        return agotados;
    }

    public Repuesto buscarPorId(String id) {
        return inventario.get(id);
    }

    
    public synchronized void asignarRepuestoAOrden(Orden orden, String idRepuesto, int cantidad) throws Exception {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad solicitada debe ser mayor que cero.");
        }

        Repuesto repuesto = inventario.get(idRepuesto);
        if (repuesto == null) {
            throw new Exception("El repuesto solicitado no existe.");
        }

        if (repuesto.getStock() < cantidad) {
            throw new Exception("Operación rechazada: La cantidad solicitada (" + cantidad 
                + ") supera la existencia disponible (" + repuesto.getStock() + ").");
        }

        try {
            repuesto.setStock(repuesto.getStock() - cantidad);
            orden.getRepuestosAsignados().put(repuesto, 
                orden.getRepuestosAsignados().getOrDefault(repuesto, 0) + cantidad);
        } catch (Exception e) {
            throw new Exception("Error durante el proceso. Transacción cancelada: " + e.getMessage());
        }
    }

    public synchronized void eliminarRepuestoDeOrden(Orden orden, Repuesto repuesto) {
        if (orden.getRepuestosAsignados().containsKey(repuesto)) {
            int cantidadDevuelta = orden.getRepuestosAsignados().remove(repuesto);
            repuesto.setStock(repuesto.getStock() + cantidadDevuelta);
        }
    }

    
    public void exportarCSV(String rutaArchivo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            for (Repuesto r : inventario.values()) {
                bw.write(r.toCSV());
                bw.newLine();
            }
        }
    }

   
    public void importarCSV(String rutaArchivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 4) {
                    String id = datos[0].trim();
                    String nombre = datos[1].trim();
                    double precio = Double.parseDouble(datos[2].trim());
                    int stock = Integer.parseInt(datos[3].trim());

                    if (precio >= 0 && stock >= 0) {
                        inventario.put(id, new Repuesto(id, nombre, precio, stock));
                    }
                }
            }
        }
    }
}

