package Dao;

import Conexion.ConexionBD;
import modelo.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gustavo-fuentes
 */
public class VehiculoDao {
   
    public boolean existePlaca(String placa) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vehiculo WHERE UPPER(placa) = UPPER(?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean tieneOrdenesAsociadas(String placa) throws SQLException {
        String sql = "SELECT COUNT(*) FROM orden_trabajo WHERE UPPER(placa) = UPPER(?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean registrar(Vehiculo v) throws SQLException {
        if (existePlaca(v.getPlaca())) {
            throw new SQLException("Ya existe un vehículo registrado con la misma placa.");
        }
        String sql = "INSERT INTO vehiculo (placa, marca, modelo, anio, id_cliente) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setInt(4, v.getAnio());
            ps.setInt(5, v.getIdCliente());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(String placa) throws SQLException {
        if (tieneOrdenesAsociadas(placa)) {
            throw new SQLException("No se puede eliminar el vehículo porque tiene órdenes de trabajo registradas.");
        }
        String sql = "DELETE FROM vehiculo WHERE UPPER(placa) = UPPER(?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Vehiculo> listarPorCliente(int idCliente) throws SQLException {
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT placa, marca, modelo, anio, id_cliente FROM vehiculo WHERE id_cliente = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehiculo v = new Vehiculo();
                    v.setPlaca(rs.getString("placa"));
                    v.setMarca(rs.getString("marca"));
                    v.setModelo(rs.getString("modelo"));
                    v.setAnio(rs.getInt("anio"));
                    v.setIdCliente(rs.getInt("id_cliente"));
                    lista.add(v);
                }
            }
        }
        return lista;
    }
    
    
    
}
