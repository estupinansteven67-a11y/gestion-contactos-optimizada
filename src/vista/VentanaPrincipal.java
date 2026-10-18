package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    public JTabbedPane tabbedPane;
    public JTextField txtNombre, txtTelefono, txtEmail, txtBuscar;
    public JCheckBox chbFavorito;
    public JComboBox<String> cmbCategoria;
    public JButton btnAgregar, btnEditar, btnEliminar, btnExportar;
    public JTable tablaContactos;
    public DefaultTableModel modeloTabla;
    public TableRowSorter<DefaultTableModel> sorter;
    public JProgressBar progressBar;
    public JPopupMenu popupMenu;
    public JMenuItem itemEliminar, itemEditar;
    public JLabel lblTotalContactos, lblTotalFavoritos;

    public VentanaPrincipal() {
        setTitle("Gestión de Contactos - Optimizado");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // ------------------ PESTAÑA 1: CONTACTOS ------------------
        JPanel panelContactos = new JPanel(new BorderLayout(10, 10));

        // Formulario
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Contacto"));

        txtNombre = new JTextField();
        txtTelefono = new JTextField();
        txtEmail = new JTextField();
        cmbCategoria = new JComboBox<>(new String[]{"Elija una Categoria", "Familia", "Amigos", "Trabajo"});
        chbFavorito = new JCheckBox("Contacto Favorito");

        panelFormulario.add(new JLabel("NOMBRES:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("TELÉFONO:"));
        panelFormulario.add(txtTelefono);
        panelFormulario.add(new JLabel("EMAIL:"));
        panelFormulario.add(txtEmail);
        panelFormulario.add(new JLabel("CATEGORÍA:"));
        panelFormulario.add(cmbCategoria);
        panelFormulario.add(new JLabel("FAVORITO:"));
        panelFormulario.add(chbFavorito);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnAgregar = new JButton("AGREGAR (Alt+A)");
        btnAgregar.setMnemonic('A'); // Atajo de teclado Alt + A
        btnEditar = new JButton("MODIFICAR");
        btnEliminar = new JButton("ELIMINAR");
        btnExportar = new JButton("EXPORTAR CSV");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnExportar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        // Búsqueda y Tabla
        JPanel panelTabla = new JPanel(new BorderLayout(5, 5));
        panelTabla.setBorder(BorderFactory.createTitledBorder("Lista de Contactos"));

        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBuscar = new JTextField(20);
        panelBuscar.add(new JLabel("Buscar por Nombre:"));
        panelBuscar.add(txtBuscar);

        String[] columnas = {"Nombre", "Teléfono", "Email", "Categoría", "Favorito"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaContactos = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tablaContactos.setRowSorter(sorter); // Ordenamiento de columnas

        JScrollPane scrollTabla = new JScrollPane(tablaContactos);

        panelTabla.add(panelBuscar, BorderLayout.NORTH);
        panelTabla.add(scrollTabla, BorderLayout.CENTER);

        // Barra de progreso en el sur
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        panelContactos.add(panelSuperior, BorderLayout.NORTH);
        panelContactos.add(panelTabla, BorderLayout.CENTER);
        panelContactos.add(progressBar, BorderLayout.SOUTH);

        // Menú Contextual (Clic derecho)
        popupMenu = new JPopupMenu();
        itemEditar = new JMenuItem("Cargar Datos para Editar");
        itemEliminar = new JMenuItem("Eliminar Contacto");
        popupMenu.add(itemEditar);
        popupMenu.add(itemEliminar);
        tablaContactos.setComponentPopupMenu(popupMenu);

        // ------------------ PESTAÑA 2: ESTADÍSTICAS ------------------
        JPanel panelEstadisticas = new JPanel(new GridLayout(2, 1, 10, 10));
        lblTotalContactos = new JLabel("Total de Contactos: 0", SwingConstants.CENTER);
        lblTotalFavoritos = new JLabel("Total de Favoritos: 0", SwingConstants.CENTER);

        lblTotalContactos.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalFavoritos.setFont(new Font("Arial", Font.BOLD, 18));

        panelEstadisticas.add(lblTotalContactos);
        panelEstadisticas.add(lblTotalFavoritos);

        // Agregar Pestañas
        tabbedPane.addTab("Contactos", panelContactos);
        tabbedPane.addTab("Estadísticas", panelEstadisticas);

        add(tabbedPane);
    }
}