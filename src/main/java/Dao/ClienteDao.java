package Dao;

import Conexion.ConexionBD;
import modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gustavo-fuentes
 */
public class ClienteDao {
   
     public boolean existeCliente(int idCliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cliente WHERE id_cliente = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean tieneVehiculosAsociados(int idCliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vehiculo WHERE id_cliente = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean registrar(Cliente c) throws SQLException {
        if (existeCliente(c.getIdCliente())) {
            throw new SQLException("El ID de cliente ya se encuentra registrado.");
        }
        String sql = "INSERT INTO cliente (id_cliente, nombre, telefono, email) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCliente());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getCorreo());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nombre=?, telefono=?, correo=? WHERE id_cliente=?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getCorreo());
            ps.setInt(4, c.getIdCliente());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idCliente) throws SQLException {
        if (tieneVehiculosAsociados(idCliente)) {
            throw new SQLException("No se puede eliminar el cliente porque tiene vehÃculos asociados.");
        }
        String sql = "DELETE FROM cliente WHERE id_cliente=?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, telefono, email FROM cliente";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                c.setTelefono(rs.getString("telefono"));
                c.setCorreo(rs.getString("email"));
                lista.add(c);
            }
        }
        return lista;
    }
}


