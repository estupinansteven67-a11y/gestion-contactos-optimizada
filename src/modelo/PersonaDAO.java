package modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {
    private final String rutaArchivo = "c:/gestionContactos/datosContactos.csv";

    public PersonaDAO() {
        File directorio = new File("c:/gestionContactos");
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }

    public boolean guardarContacto(Persona persona) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo, true))) {
            String linea = String.format("%s,%s,%s,%s,%b",
                    persona.getNombre(), persona.getTelefono(), persona.getEmail(),
                    persona.getCategoria(), persona.isFavorito());
            writer.write(linea);
            writer.newLine();
            return true;
        }
    }

    public List<Persona> leerContactos() throws IOException {
        List<Persona> lista = new ArrayList<>();
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) return lista;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 5) {
                    lista.add(new Persona(datos[0], datos[1], datos[2], datos[3], Boolean.parseBoolean(datos[4])));
                }
            }
        }
        return lista;
    }

    public void reescribirTodos(List<Persona> lista) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo, false))) {
            for (Persona p : lista) {
                String linea = String.format("%s,%s,%s,%s,%b",
                        p.getNombre(), p.getTelefono(), p.getEmail(), p.getCategoria(), p.isFavorito());
                writer.write(linea);
                writer.newLine();
            }
        }
    }
}