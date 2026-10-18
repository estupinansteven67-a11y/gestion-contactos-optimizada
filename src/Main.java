import controlador.LogicaContactos;
import modelo.PersonaDAO;
import vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal vista = new VentanaPrincipal();
            PersonaDAO dao = new PersonaDAO();
            new LogicaContactos(vista, dao);
            vista.setVisible(true);
        });
    }
}