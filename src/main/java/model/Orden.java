package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gustavo-fuentes
 */
public class Orden {
    private String idOrden;
    private List<Servicio> servicios = new ArrayList<>();
    private Map<Repuesto, Integer> repuestosAsignados = new HashMap<>();

    public Orden(String idOrden) {
        this.idOrden = idOrden;
    }

    public String getIdOrden() { return idOrden; }

    public void agregarServicio(Servicio servicio) {
        servicios.add(servicio);
    }

    public List<Servicio> getServicios() { return servicios; }
    public Map<Repuesto, Integer> getRepuestosAsignados() { return repuestosAsignados; }

    
    public double calcularCostoTotalManoDeObra() {
        double totalServicios = 0.0;
        for (Servicio s : servicios) {
            totalServicios += s.getCosto();
        }
        return totalServicios;
    }

    
    public double calcularCostoTotalRepuestos() {
        double totalRepuestos = 0.0;
        for (Map.Entry<Repuesto, Integer> entry : repuestosAsignados.entrySet()) {
            totalRepuestos += entry.getKey().getPrecio() * entry.getValue();
        }
        return totalRepuestos;
    }

    public double calcularCostoTotalGeneral() {
        return calcularCostoTotalManoDeObra() + calcularCostoTotalRepuestos();
    }
}
