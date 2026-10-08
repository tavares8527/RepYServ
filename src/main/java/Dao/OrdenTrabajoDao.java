package Dao;

import Conexion.ConexionBD;
import modelo.OrdenTrabajo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gustavo-fuentes
 */
public class OrdenTrabajoDao {
   
    public int registrarOrden(OrdenTrabajo ot) throws SQLException {
        String sql = "INSERT INTO orden_trabajo (id_cliente, placa, id_mecanico, fecha, estado, total) VALUES (?, ?, ?, SYSDATE, 'PENDIENTE', 0)";
        String cols[] = {"id_orden"};
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, cols)) {
            ps.setInt(1, ot.getIdCliente());
            ps.setString(2, ot.getPlaca());
            ps.setInt(3, ot.getIdMecanico());
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public boolean agregarServicioAOrden(int idOrden, int idServicio, double precio) throws SQLException {
        String sql = "INSERT INTO detalle_servicio (id_orden, id_servicio, precio) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            ps.setInt(2, idServicio);
            ps.setDouble(3, precio);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean agregarRepuestoAOrden(int idOrden, int idRepuesto, int cantidad, double precio) throws SQLException {
        
        String checkStock = "SELECT stock FROM repuesto WHERE id_repuesto = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement psStock = con.prepareStatement(checkStock)) {
            psStock.setInt(1, idRepuesto);
            try (ResultSet rs = psStock.executeQuery()) {
                if (rs.next()) {
                    int stockActual = rs.getInt("stock");
                    if (stockActual < cantidad) {
                        throw new SQLException("Stock insuficiente. Stock disponible: " + stockActual);
                    }
                }
            }

           
            String sqlDetail = "INSERT INTO detalle_repuesto (id_orden, id_repuesto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
            try (PreparedStatement psDetail = con.prepareStatement(sqlDetail)) {
                psDetail.setInt(1, idOrden);
                psDetail.setInt(2, idRepuesto);
                psDetail.setInt(3, cantidad);
                psDetail.setDouble(4, precio);
                psDetail.executeUpdate();
            }

           
            String sqlUpdateStock = "UPDATE repuesto SET stock = stock - ? WHERE id_repuesto = ?";
            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdateStock)) {
                psUpdate.setInt(1, cantidad);
                psUpdate.setInt(2, idRepuesto);
                psUpdate.executeUpdate();
            }
        }
        return true;
    }

    public void recalcularTotalOrden(int idOrden) throws SQLException {
        String sqlTotalServicios = "SELECT NVL(SUM(precio), 0) FROM detalle_servicio WHERE id_orden = ?";
        String sqlTotalRepuestos = "SELECT NVL(SUM(cantidad * precio_unitario), 0) FROM detalle_repuesto WHERE id_orden = ?";
        
        double totalServicios = 0;
        double totalRepuestos = 0;

        try (Connection con = ConexionBD.getConexion()) {
            try (PreparedStatement ps1 = con.prepareStatement(sqlTotalServicios)) {
                ps1.setInt(1, idOrden);
                try (ResultSet rs1 = ps1.executeQuery()) {
                    if (rs1.next()) totalServicios = rs1.getDouble(1);
                }
            }

            try (PreparedStatement ps2 = con.prepareStatement(sqlTotalRepuestos)) {
                ps2.setInt(1, idOrden);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next()) totalRepuestos = rs2.getDouble(1);
                }
            }

            double totalGeneral = totalServicios + totalRepuestos;
            String sqlUpdate = "UPDATE orden_trabajo SET total = ? WHERE id_orden = ?";
            try (PreparedStatement psUp = con.prepareStatement(sqlUpdate)) {
                psUp.setDouble(1, totalGeneral);
                psUp.setInt(2, idOrden);
                psUp.executeUpdate();
            }
        }
    }
    
}

