package dao;

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
        inventario.put(r.getId(), r);
    }

        public void abastecer(String id, int cantidad) throws Exception {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a abastecer debe ser mayor a cero.");
        }
        Repuesto r = inventario.get(id);
        if (r != null) {
            r.setStock(r.getStock() + cantidad);
        } else {
            throw new Exception("Repuesto no encontrado.");
        }
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
                        Repuesto r = new Repuesto(id, nombre, precio, stock);
                        agregarRepuesto(r);
                    }
                }
            }
        }
    }
}

