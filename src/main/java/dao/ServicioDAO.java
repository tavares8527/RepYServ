package dao;

import model.Servicio;
import java.io.*;
import java.util.*;

/**
 *
 * @author gustavo-fuentes
 */
public class ServicioDAO {
    private Map<String, Servicio> catalogo = new HashMap<>();

    public void agregarServicio(Servicio s) {
        catalogo.put(s.getId(), s);
    }

    public Servicio buscarPorId(String id) {
        return catalogo.get(id);
    }

    public List<Servicio> obtenerServicios() {
        return new ArrayList<>(catalogo.values());
    }

    
    public void exportarCSV(String rutaArchivo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            for (Servicio s : catalogo.values()) {
                bw.write(s.toCSV());
                bw.newLine();
            }
        }
    }

    
    public void importarCSV(String rutaArchivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 3) {
                    String id = datos[0].trim();
                    String descripcion = datos[1].trim();
                    double costo = Double.parseDouble(datos[2].trim());

                    if (costo >= 0) {
                        agregarServicio(new Servicio(id, descripcion, costo));
                    }
                }
            }
        }
    }
}