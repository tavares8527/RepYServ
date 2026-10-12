package model;

import vista.FrmPrincipal;
import javax.swing.SwingUtilities;

/**
 *
 * @author gustavo-fuentes
 */
public class Modelo {
    
     public static void main(String[] args) {
                
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo cargar el estilo visual.");
        }

        
        SwingUtilities.invokeLater(() -> {
            new FrmPrincipal().setVisible(true);
        });
    }
        
        
    }

