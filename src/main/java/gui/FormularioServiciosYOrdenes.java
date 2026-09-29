package gui;

import dao.RepuestoDAO;
import dao.ServicioDAO;
import model.Orden;
import model.Repuesto;
import model.Servicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;


/**
 *
 * @author gustavo-fuentes
 */
public class FormularioServiciosYOrdenes extends JFrame {
    private ServicioDAO servicioDAO = new ServicioDAO();
    private RepuestoDAO repuestoDAO = new RepuestoDAO();
    private Orden ordenActual = new Orden("ORD-001");

    
    private DefaultTableModel modeloServicios;
    private JTable tablaServicios;

    
    private DefaultTableModel modeloRepuestos;
    private JTable tablaRepuestos;

    
    private DefaultTableModel modeloOrdenServicios;
    private DefaultTableModel modeloOrdenRepuestos;
    private JLabel lblTotalManoObra;
    private JLabel lblTotalGeneral;

    public FormularioServiciosYOrdenes() {
        setTitle("Sistema de Gestión de Servicios, Repuestos y Órdenes");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        servicioDAO.agregarServicio(new Servicio("S1", "Cambio de aceite", 100.0));
        servicioDAO.agregarServicio(new Servicio("S2", "Revisión de frenos", 150.0));
        servicioDAO.agregarServicio(new Servicio("S3", "Alineación", 125.0));

        repuestoDAO.agregarRepuesto(new Repuesto("R1", "Filtro de Aceite", 50.0, 10));
        repuestoDAO.agregarRepuesto(new Repuesto("R2", "Pastillas de Freno", 200.0, 5));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Órdenes de Trabajo", crearPanelOrden());
        tabbedPane.addTab("Catálogo de Servicios", crearPanelServicios());
        tabbedPane.addTab("Inventario de Repuestos", crearPanelRepuestos());

        add(tabbedPane);
        actualizarTablas();
    }

    
    private JPanel crearPanelOrden() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        
        JPanel panelTablas = new JPanel(new GridLayout(2, 1, 5, 5));

        modeloOrdenServicios = new DefaultTableModel(new String[]{"ID", "Servicio", "Costo (Mano de Obra)"}, 0);
        JTable tablaOrdServ = new JTable(modeloOrdenServicios);
        JPanel pnlServ = new JPanel(new BorderLayout());
        pnlServ.setBorder(BorderFactory.createTitledBorder("Servicios en la Orden"));
        pnlServ.add(new JScrollPane(tablaOrdServ), BorderLayout.CENTER);

        modeloOrdenRepuestos = new DefaultTableModel(new String[]{"ID", "Repuesto", "Precio Unitario", "Cantidad"}, 0);
        JTable tablaOrdRep = new JTable(modeloOrdenRepuestos);
        JPanel pnlRep = new JPanel(new BorderLayout());
        pnlRep.setBorder(BorderFactory.createTitledBorder("Repuestos Asignados a la Orden"));
        pnlRep.add(new JScrollPane(tablaOrdRep), BorderLayout.CENTER);

        panelTablas.add(pnlServ);
        panelTablas.add(pnlRep);
        panel.add(panelTablas, BorderLayout.CENTER);

       
        JPanel panelAcciones = new JPanel(new FlowLayout());
        JButton btnAgregarServ = new JButton("Agregar Servicio a Orden");
        JButton btnAgregarRep = new JButton("Asignar Repuesto a Orden");
        JButton btnEliminarRep = new JButton("Eliminar Repuesto de Orden");

        panelAcciones.add(btnAgregarServ);
        panelAcciones.add(btnAgregarRep);
        panelAcciones.add(btnEliminarRep);
        panel.add(panelAcciones, BorderLayout.NORTH);

        
        JPanel panelTotales = new JPanel(new GridLayout(2, 1));
        lblTotalManoObra = new JLabel("Costo Total Mano de Obra: Q0.00", SwingConstants.RIGHT);
        lblTotalManoObra.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalGeneral = new JLabel("Costo Total General: Q0.00", SwingConstants.RIGHT);
        lblTotalGeneral.setFont(new Font("Arial", Font.BOLD, 14));

        panelTotales.add(lblTotalManoObra);
        panelTotales.add(lblTotalGeneral);
        panel.add(panelTotales, BorderLayout.SOUTH);

        
        btnAgregarServ.addActionListener(e -> agregarServicioAOrden());
        btnAgregarRep.addActionListener(e -> asignarRepuestoAOrden());
        btnEliminarRep.addActionListener(e -> eliminarRepuestoDeOrden());

        return panel;
    }

    private void agregarServicioAOrden() {
        String id = JOptionPane.showInputDialog(this, "Ingrese el ID del Servicio a agregar (Ej: S1, S2, S3):");
        if (id == null || id.trim().isEmpty()) return;

        Servicio s = servicioDAO.buscarPorId(id.trim());
        if (s != null) {
            ordenActual.agregarServicio(s);
            actualizarTablas();
        } else {
            JOptionPane.showMessageDialog(this, "Servicio no encontrado en el catálogo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void asignarRepuestoAOrden() {
        try {
            String id = JOptionPane.showInputDialog(this, "Ingrese el ID del Repuesto (Ej: R1, R2):");
            if (id == null || id.trim().isEmpty()) return;

            String cantStr = JOptionPane.showInputDialog(this, "Ingrese la cantidad requerida:");
            if (cantStr == null) return;
            int cantidad = Integer.parseInt(cantStr.trim());

            
            repuestoDAO.asignarRepuestoAOrden(ordenActual, id.trim(), cantidad);
            actualizarTablas();
            JOptionPane.showMessageDialog(this, "Repuesto asignado correctamente y stock actualizado.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación/Transacción", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarRepuestoDeOrden() {
        String id = JOptionPane.showInputDialog(this, "Ingrese el ID del Repuesto a eliminar de la orden:");
        if (id == null || id.trim().isEmpty()) return;

        Repuesto r = repuestoDAO.buscarPorId(id.trim());
        if (r != null && ordenActual.getRepuestosAsignados().containsKey(r)) {
            repuestoDAO.eliminarRepuestoDeOrden(ordenActual, r);
            actualizarTablas();
            JOptionPane.showMessageDialog(this, "Repuesto eliminado. Unidades devueltas al inventario.");
        } else {
            JOptionPane.showMessageDialog(this, "El repuesto no está asignado a esta orden.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private JPanel crearPanelServicios() {
        JPanel panel = new JPanel(new BorderLayout());
        modeloServicios = new DefaultTableModel(new String[]{"ID", "Descripción", "Costo"}, 0);
        tablaServicios = new JTable(modeloServicios);
        panel.add(new JScrollPane(tablaServicios), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnNuevo = new JButton("Nuevo Servicio");
        JButton btnExportar = new JButton("Exportar CSV");
        JButton btnImportar = new JButton("Importar CSV");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnExportar);
        panelBotones.add(btnImportar);
        panel.add(panelBotones, BorderLayout.SOUTH);

        btnNuevo.addActionListener(e -> {
            try {
                String id = JOptionPane.showInputDialog(this, "ID Servicio:");
                if (id == null || id.trim().isEmpty()) return;

                String desc = JOptionPane.showInputDialog(this, "Descripción:");
                double costo = Double.parseDouble(JOptionPane.showInputDialog(this, "Costo (Q):"));

                servicioDAO.agregarServicio(new Servicio(id.trim(), desc, costo));
                actualizarTablas();
                JOptionPane.showMessageDialog(this, "Servicio registrado con éxito.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en los datos ingresados: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnExportar.addActionListener(e -> exportarCSV(servicioDAO, "servicios.csv"));
        btnImportar.addActionListener(e -> importarCSVServicios());

        return panel;
    }

    
    private JPanel crearPanelRepuestos() {
        JPanel panel = new JPanel(new BorderLayout());
        modeloRepuestos = new DefaultTableModel(new String[]{"ID", "Nombre", "Precio", "Stock (Existencias)"}, 0);
        tablaRepuestos = new JTable(modeloRepuestos);
        panel.add(new JScrollPane(tablaRepuestos), BorderLayout.CENTER);

        
        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnNuevo = new JButton("Registrar Nuevo");
        JButton btnModificar = new JButton("Modificar Información");
        JButton btnAbastecer = new JButton("Abastecer Stock");
        JButton btnExistencias = new JButton("Consultar Existencias");
        JButton btnAgotados = new JButton("Ver Agotados");
        JButton btnExportar = new JButton("Exportar CSV");
        JButton btnImportar = new JButton("Importar CSV");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnModificar);
        panelBotones.add(btnAbastecer);
        panelBotones.add(btnExistencias);
        panelBotones.add(btnAgotados);
        panelBotones.add(btnExportar);
        panelBotones.add(btnImportar);

        panel.add(panelBotones, BorderLayout.SOUTH);

        
        btnNuevo.addActionListener(e -> {
            try {
                String id = JOptionPane.showInputDialog(this, "ID Repuesto:");
                if (id == null || id.trim().isEmpty()) return;

                String nombre = JOptionPane.showInputDialog(this, "Nombre del Repuesto:");
                double precio = Double.parseDouble(JOptionPane.showInputDialog(this, "Precio (Q):"));
                int stock = Integer.parseInt(JOptionPane.showInputDialog(this, "Stock Inicial:"));

                repuestoDAO.agregarRepuesto(new Repuesto(id.trim(), nombre, precio, stock));
                actualizarTablas();
                JOptionPane.showMessageDialog(this, "Repuesto registrado exitosamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error de validación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        
        btnModificar.addActionListener(e -> {
            try {
                String id = JOptionPane.showInputDialog(this, "ID del Repuesto a modificar:");
                if (id == null || id.trim().isEmpty()) return;

                Repuesto r = repuestoDAO.buscarPorId(id.trim());
                if (r == null) {
                    JOptionPane.showMessageDialog(this, "Repuesto no encontrado.");
                    return;
                }

                String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo Nombre:", r.getNombre());
                String nuevoPrecioStr = JOptionPane.showInputDialog(this, "Nuevo Precio (Q):", r.getPrecio());
                if (nuevoPrecioStr == null) return;

                double nuevoPrecio = Double.parseDouble(nuevoPrecioStr.trim());

                repuestoDAO.modificarRepuesto(r.getId(), nuevoNombre, nuevoPrecio);
                actualizarTablas();
                JOptionPane.showMessageDialog(this, "Información modificada correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al modificar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        
        btnAbastecer.addActionListener(e -> {
            try {
                String id = JOptionPane.showInputDialog(this, "ID del Repuesto a abastecer:");
                if (id == null || id.trim().isEmpty()) return;

                String cantStr = JOptionPane.showInputDialog(this, "Cantidad a ingresar (debe ser mayor a 0):");
                if (cantStr == null) return;

                int cantidad = Integer.parseInt(cantStr.trim());

                repuestoDAO.abastecer(id.trim(), cantidad);
                actualizarTablas();
                JOptionPane.showMessageDialog(this, "Inventario abastecido exitosamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en abastecimiento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

      
        btnExistencias.addActionListener(e -> actualizarTablas());

        
        btnAgotados.addActionListener(e -> {
            modeloRepuestos.setRowCount(0);
            List<Repuesto> agotados = repuestoDAO.obtenerAgotados();
            if (agotados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay repuestos agotados en este momento.");
                actualizarTablas();
            } else {
                for (Repuesto r : agotados) {
                    modeloRepuestos.addRow(new Object[]{r.getId(), r.getNombre(), "Q" + r.getPrecio(), r.getStock()});
                }
            }
        });

        btnExportar.addActionListener(e -> exportarCSV(repuestoDAO, "repuestos.csv"));
        btnImportar.addActionListener(e -> importarCSVRepuestos());

        return panel;
    }

    
    private void actualizarTablas() {
        
        modeloServicios.setRowCount(0);
        for (Servicio s : servicioDAO.obtenerServicios()) {
            modeloServicios.addRow(new Object[]{s.getId(), s.getDescripcion(), "Q" + s.getCosto()});
        }

        
        modeloRepuestos.setRowCount(0);
        for (Repuesto r : repuestoDAO.obtenerExistencias()) {
            modeloRepuestos.addRow(new Object[]{r.getId(), r.getNombre(), "Q" + r.getPrecio(), r.getStock()});
        }

        
        modeloOrdenServicios.setRowCount(0);
        for (Servicio s : ordenActual.getServicios()) {
            modeloOrdenServicios.addRow(new Object[]{s.getId(), s.getDescripcion(), "Q" + s.getCosto()});
        }

        modeloOrdenRepuestos.setRowCount(0);
        for (Map.Entry<Repuesto, Integer> entry : ordenActual.getRepuestosAsignados().entrySet()) {
            modeloOrdenRepuestos.addRow(new Object[]{
                entry.getKey().getId(), entry.getKey().getNombre(), "Q" + entry.getKey().getPrecio(), entry.getValue()
            });
        }

        
        lblTotalManoObra.setText(String.format("Costo Total Mano de Obra: Q%.2f", ordenActual.calcularCostoTotalManoDeObra()));
        lblTotalGeneral.setText(String.format("Costo Total General: Q%.2f", ordenActual.calcularCostoTotalGeneral()));
    }

    private void exportarCSV(Object dao, String nombrePredeterminado) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(nombrePredeterminado));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                if (dao instanceof ServicioDAO) ((ServicioDAO) dao).exportarCSV(fc.getSelectedFile().getAbsolutePath());
                if (dao instanceof RepuestoDAO) ((RepuestoDAO) dao).exportarCSV(fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Exportación CSV completada.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importarCSVServicios() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                servicioDAO.importarCSV(fc.getSelectedFile().getAbsolutePath());
                actualizarTablas();
                JOptionPane.showMessageDialog(this, "Servicios importados correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al importar CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importarCSVRepuestos() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                repuestoDAO.importarCSV(fc.getSelectedFile().getAbsolutePath());
                actualizarTablas();
                JOptionPane.showMessageDialog(this, "Repuestos importados correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al importar CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormularioServiciosYOrdenes().setVisible(true));
    }
}
