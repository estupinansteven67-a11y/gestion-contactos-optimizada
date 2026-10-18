package controlador;

import modelo.Persona;
import modelo.PersonaDAO;
import vista.VentanaPrincipal;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LogicaContactos {
    private VentanaPrincipal vista;
    private PersonaDAO dao;
    private List<Persona> listaMemoria;

    public LogicaContactos(VentanaPrincipal vista, PersonaDAO dao) {
        this.vista = vista;
        this.dao = dao;
        this.listaMemoria = new ArrayList<>();

        // Registrar Eventos
        this.vista.btnAgregar.addActionListener(e -> agregarContacto());
        this.vista.btnEditar.addActionListener(e -> editarContacto());
        this.vista.btnEliminar.addActionListener(e -> eliminarContacto());
        this.vista.btnExportar.addActionListener(e -> exportarCSV());

        this.vista.itemEditar.addActionListener(e -> cargarSeleccionEnFormulario());
        this.vista.itemEliminar.addActionListener(e -> eliminarContacto());

        // Evento de búsqueda dinámica
        this.vista.txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }

            private void filtrar() {
                String texto = vista.txtBuscar.getText();
                if (texto.trim().length() == 0) {
                    vista.sorter.setRowFilter(null);
                } else {
                    vista.sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0));
                }
            }
        });

        // Evento de selección en tabla
        this.vista.tablaContactos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && vista.tablaContactos.getSelectedRow() != -1) {
                cargarSeleccionEnFormulario();
            }
        });

        // Cargar datos al iniciar
        cargarContactosSimulado();
    }

    private void cargarContactosSimulado() {
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i += 20) {
                    Thread.sleep(100);
                    publish(i);
                }
                listaMemoria = dao.leerContactos();
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                vista.progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                actualizarTabla();
                vista.progressBar.setString("Carga Completa");
            }
        };
        worker.execute();
    }

    private void agregarContacto() {
        if (!validarFormulario()) return;

        Persona p = new Persona(
                vista.txtNombre.getText(),
                vista.txtTelefono.getText(),
                vista.txtEmail.getText(),
                vista.cmbCategoria.getSelectedItem().toString(),
                vista.chbFavorito.isSelected()
        );

        try {
            dao.guardarContacto(p);
            listaMemoria.add(p);
            actualizarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(vista, "Contacto Registrado!!!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(vista, "Error al guardar el archivo: " + ex.getMessage());
        }
    }

    private void editarContacto() {
        int fila = vista.tablaContactos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un contacto para editar.");
            return;
        }

        int filaReal = vista.tablaContactos.convertRowIndexToModel(fila);
        if (!validarFormulario()) return;

        Persona p = listaMemoria.get(filaReal);
        p.setNombre(vista.txtNombre.getText());
        p.setTelefono(vista.txtTelefono.getText());
        p.setEmail(vista.txtEmail.getText());
        p.setCategoria(vista.cmbCategoria.getSelectedItem().toString());
        p.setFavorito(vista.chbFavorito.isSelected());

        try {
            dao.reescribirTodos(listaMemoria);
            actualizarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(vista, "Contacto Actualizado!!!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(vista, "Error al actualizar archivo.");
        }
    }

    private void eliminarContacto() {
        int fila = vista.tablaContactos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un contacto para eliminar.");
            return;
        }

        int res = JOptionPane.showConfirmDialog(vista, "¿Está seguro de eliminar este contacto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            int filaReal = vista.tablaContactos.convertRowIndexToModel(fila);
            listaMemoria.remove(filaReal);
            try {
                dao.reescribirTodos(listaMemoria);
                actualizarTabla();
                limpiarCampos();
                JOptionPane.showMessageDialog(vista, "Contacto Eliminado.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(vista, "Error al eliminar en archivo.");
            }
        }
    }

    private void exportarCSV() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
            File dest = chooser.getSelectedFile();
            if (!dest.getName().endsWith(".csv")) {
                dest = new File(dest.getAbsolutePath() + ".csv");
            }
            try (PrintWriter pw = new PrintWriter(new FileWriter(dest))) {
                pw.println("Nombre,Telefono,Email,Categoria,Favorito");
                for (Persona p : listaMemoria) {
                    pw.println(String.format("%s,%s,%s,%s,%b", p.getNombre(), p.getTelefono(), p.getEmail(), p.getCategoria(), p.isFavorito()));
                }
                JOptionPane.showMessageDialog(vista, "Exportado correctamente a CSV.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(vista, "Error al exportar: " + ex.getMessage());
            }
        }
    }

    private void cargarSeleccionEnFormulario() {
        int fila = vista.tablaContactos.getSelectedRow();
        if (fila != -1) {
            int filaReal = vista.tablaContactos.convertRowIndexToModel(fila);
            Persona p = listaMemoria.get(filaReal);
            vista.txtNombre.setText(p.getNombre());
            vista.txtTelefono.setText(p.getTelefono());
            vista.txtEmail.setText(p.getEmail());
            vista.cmbCategoria.setSelectedItem(p.getCategoria());
            vista.chbFavorito.setSelected(p.isFavorito());
        }
    }

    private void actualizarTabla() {
        vista.modeloTabla.setRowCount(0);
        int favCount = 0;
        for (Persona p : listaMemoria) {
            vista.modeloTabla.addRow(new Object[]{p.getNombre(), p.getTelefono(), p.getEmail(), p.getCategoria(), p.isFavorito() ? "Sí" : "No"});
            if (p.isFavorito()) favCount++;
        }
        vista.lblTotalContactos.setText("Total de Contactos: " + listaMemoria.size());
        vista.lblTotalFavoritos.setText("Total de Favoritos: " + favCount);
    }

    private boolean validarFormulario() {
        if (vista.txtNombre.getText().trim().isEmpty() ||
            vista.txtTelefono.getText().trim().isEmpty() ||
            vista.txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Todos los campos deben ser llenados!!!");
            return false;
        }
        if (vista.cmbCategoria.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(vista, "Elija una Categoria!!!");
            return false;
        }
        return true;
    }

    private void limpiarCampos() {
        vista.txtNombre.setText("");
        vista.txtTelefono.setText("");
        vista.txtEmail.setText("");
        vista.cmbCategoria.setSelectedIndex(0);
        vista.chbFavorito.setSelected(false);
        vista.tablaContactos.clearSelection();
    }
}