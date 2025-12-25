/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package libros;

import java.util.Date;

/**
 *
 * @author Usuario
 */
public class GestionLibros {
    /*
    private LibroDAO libroDAO = new LibroDAO();
    private EjemplarDAO ejemplarDAO = new EjemplarDAO();
    private IncidenciaEjemplarDAO incidenciaDAO = new IncidenciaEjemplarDAO();

    // RF-1.1
    public void darAltaLibro(Libro libro) throws Exception {
        if (libro.getFechaPublicacion().after(new Date())) {
            throw new Exception("Fecha de publicación incorrecta");
        }
        libroDAO.insertarLibro(libro);
    }

    // RF-1.2
    public int darAltaEjemplar(String isbn) throws Exception {
        if (!libroDAO.existeLibro(isbn)) {
            throw new Exception("El libro no existe");
        }
        int cod = ejemplarDAO.generarNuevoCodigoEjemplar(isbn);
        ejemplarDAO.insertarEjemplar(isbn, cod, EstadoEjemplar.DISPONIBLE);
        return cod;
    }

    // RF-1.3
    public void darBajaEjemplar(String isbn) throws Exception {
        Integer cod = ejemplarDAO.obtenerEjemplarDisponible(isbn);
        if (cod == null) {
            throw new Exception("No hay ejemplares disponibles");
        }
        ejemplarDAO.cambiarEstadoEjemplar(isbn, cod, EstadoEjemplar.DESCATALOGADO);
    }

    // RF-1.4
    public int registrarIncidencia(String isbn, int codEjemplar,
                                   Date fechaRegistro, String descripcion,
                                   Integer prioridad, Date fechaResolucion) throws Exception {

        if (!ejemplarDAO.existeEjemplar(isbn, codEjemplar)) {
            throw new Exception("El ejemplar no existe");
        }
        if (fechaRegistro.after(new Date())) {
            throw new Exception("Fecha incorrecta");
        }
        if (fechaResolucion != null && fechaResolucion.before(fechaRegistro)) {
            throw new Exception("Resolución anterior a incidencia");
        }
        if (prioridad != null && (prioridad < 1 || prioridad > 5)) {
            throw new Exception("Prioridad inválida");
        }

        int id = incidenciaDAO.generarIdIncidencia();
        IncidenciaEjemplar inc = new IncidenciaEjemplar(
                id, isbn, codEjemplar, fechaRegistro, descripcion, prioridad, fechaResolucion
        );
        incidenciaDAO.insertarIncidencia(inc);
        return id;
    }
    */

}
