package model;



/**
 *
 * @author gustavo-fuentes
 */
public class Servicio {
    private String id;
    private String descripcion;
    private double costo;

    public Servicio(String id, String descripcion, double costo) {
        if (costo < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo.");
        }
        this.id = id;
        this.descripcion = descripcion;
        this.costo = costo;
    }

    public String getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public double getCosto() { return costo; }

    public void setCosto(double costo) {
        if (costo < 0) throw new IllegalArgumentException("Costo no puede ser negativo.");
        this.costo = costo;
    }

    public String toCSV() {
        return id + "," + descripcion + "," + costo;
    }
}
