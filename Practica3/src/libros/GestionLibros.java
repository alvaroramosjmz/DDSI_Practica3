/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package libros;

import java.sql.SQLException; // conexión activa con la DB mediante JDBC
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;
import java.util.Date;

/**
 * Clase de gestión del subsistema de control de libros.
 * 
 * Se encarga de interactuar con el usuario por consola y delegar las 
 * operaciones de acceso a datos en los DAOs.
 * 
 * @author Usuario
 */
public class GestionLibros {
    
    // DAOs del subsistema
    private LibroDAO libroDAO;
    private EjemplarDAO ejemplarDAO;
    private IncidenciaEjemplarDAO incidenciaDAO;
    
    // Scanner para leer datos que introduzca el usuario
    private Scanner sc;

    // Constructor con parámetros (recibe los DAOs ya creados e inicializa Scanner para que sea de entrada desde la terminal)
    public GestionLibros(LibroDAO libroDAO, EjemplarDAO ejemplarDAO, IncidenciaEjemplarDAO incidenciaDAO) {
        this.libroDAO = libroDAO;
        this.ejemplarDAO = ejemplarDAO;
        this.incidenciaDAO = incidenciaDAO;
        this.sc = new Scanner(System.in);
    }

    // Menú principal del subsistema Control de Libros
    public void mostrarMenu() {
        int opcion = -1;
        
        // Menu se repite hasta que el usuario elija salir
        while (opcion != 0) {
            System.out.println("\n--- GESTION DE LIBROS ---");
            System.out.println("1. Alta Libro (RF-1.1)");
            System.out.println("2. Alta Ejemplar (RF-1.2)");
            System.out.println("3. Baja Ejemplar (RF-1.3)");
            System.out.println("4. Registrar Incidencia Ejemplar (RF-1.4)");
            System.out.println("5. Listar Libros (RF-1.5)");
            System.out.println("0. Volver");
            System.out.print("Opcion: ");

            try {
                
                // Leemos la opción introducida
                opcion = Integer.parseInt(sc.nextLine());
                
                // Se ejecuta la acción que corresponda a la opción seleccionada
                switch (opcion) {
                    case 1 -> altaLibro();
                    case 2 -> altaEjemplar();
                    case 3 -> bajaEjemplar();
                    case 4 -> registrarIncidencia();
                    case 5 -> listarLibros();
                }
            } catch (Exception e) {
                // Captura de error de formato o SQL
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // RF-1.1 Alta de nuevo libro
    private void altaLibro() throws SQLException {
        
        System.out.println("\n--- ALTA LIBRO ---");
        
        // Pido ISBN al usuario
        boolean sigue = true;
        String isbn = "";
        
        while(sigue){
            System.out.print("ISBN (0 para volver): "); 
            isbn = sc.nextLine();
            
            if(isbn.equals("0")){
                return;
            }
            else{
                 // Si el libro ya existe 
                if (libroDAO.existeLibro(isbn)) {
                    System.out.println("El libro ya existe.");
                }
                else{
                    sigue = false;
                }
            }
           
        }
              
        // Leemos el resto de atributos
        
        // Título obligatorio (seguimos pidiendolo hasta que no esté vacío)
        String autor;
        do {
            System.out.print("Autor (obligatorio): ");
            autor = sc.nextLine();
            if (autor.isEmpty()) {
                System.out.println("El autor es obligatorio.");
            }
        } while (autor.isEmpty());
        
        // Autor obligatorio (seguimos pidiendolo hasta que no esté vacío)
        String titulo;
        do {
            System.out.print("Titulo (obligatorio): ");
            titulo = sc.nextLine();
            if (titulo.isEmpty()) {
                System.out.println("El titulo es obligatorio.");
            }
        } while (titulo.isEmpty());
        
        // Editorial opcional
        System.out.print("Editorial (opcional): ");
        String editorial = sc.nextLine();
        // Si pulsa enter, se deja como vacía
        if (editorial.isEmpty()) editorial = null;
        
        // Fecha de publicación opcional y con validación
        Date fechaPublicacion = null;
        boolean fechaValida = false;

        while (!fechaValida) {
            System.out.print("Fecha publicacion (dd/MM/yyyy - opcional): ");
            String fp = sc.nextLine();

            // Si pulsa ENTER → fecha opcional
            if (fp.isEmpty()) {
                fechaPublicacion = null;
                fechaValida = true;
            } else {
                try {
                    // Formato obligatorio de la fecha
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    sdf.setLenient(false); // evita fechas imposibles
                    
                    // Convertir texto a Date
                    fechaPublicacion = sdf.parse(fp);

                    // Fecha de publicación no sea posterior a fecha actual
                    if (fechaPublicacion.after(new Date())) {
                        System.out.println("La fecha no puede ser posterior a la actual.");
                    } else {
                        fechaValida = true;
                    }
                } catch (Exception e) {
                    System.out.println("Formato incorrecto. Use dd/MM/yyyy.");
                }
            }
}       
        // Número de páginas opcional 
        System.out.print("Numero paginas (opcional): ");
        String np = sc.nextLine();
        // Si no se introduce nada NULL si no se convierte a Integer
        Integer pags = null; 
        if (!np.isEmpty()) pags = Integer.valueOf(np);
        
        // Edición opcional 
        System.out.print("Edicion (opcional): ");
        String ed = sc.nextLine();
        // Si no se introduce nada NULL si no se convierte a Integer
        Integer edicion = null; 
        if (!ed.isEmpty()) edicion = Integer.valueOf(ed);
        
        // Género opcional
        System.out.print("Genero(opcional): "); 
        // Si se introduce ENTER --> se guarda NULL
        String genero = sc.nextLine();
        if (genero.isEmpty()) genero = null;
        
        // Se crea un objeto Libro con todos los atributos recogidos
        Libro libro = new Libro(isbn, autor, titulo, editorial, fechaPublicacion, pags, edicion, genero);
        
        // LibroDAO se encarga de insertarlo en la BD
        libroDAO.insertarLibro(libro);
        
        // Mensaje de confirmación
        System.out.println("Libro dado de alta correctamente.");
    }

    // RF-1.2 Alta de ejemplar
    private void altaEjemplar() throws SQLException {
        
        System.out.println("\n--- ALTA EJEMPLAR ---");
        
        String isbn = "";
        boolean sigue = true;
        
        // Se pide el ISBN del ejemplar a dar de alta hasta que sea de un libro
        //previamente registrado o usuario decida volver
        while (sigue){
           System.out.print("ISBN del libro(0 para volver): ");
            isbn = sc.nextLine();
            
            // Opción volver al menu anterior
            if(isbn.equals("0")){
                return;
            }
            
            // Si el libro no existe se informa y se pide un ISBN válido
            if (!libroDAO.existeLibro(isbn)) {
                System.out.println("El libro no existe.");
            }
            else{
                // ISBN válido --> salgo del bucle
                sigue = false;
            }
        }
        
        // Genero nuevo código de ejemplar secuencialmente
        int nuevoCodigo = ejemplarDAO.generarNuevoCodigoEjemplar(isbn);
        
        // Inserto el ejemplar en estado DISPONIBLE
        ejemplarDAO.insertarEjemplar(isbn, nuevoCodigo, EstadoEjemplar.DISPONIBLE);

        // Mensaje de confirmación
        System.out.println("Ejemplar creado con codigo: " + nuevoCodigo);
    }

    // RF-1.3 Baja de ejemplar (baja lógica)
    private void bajaEjemplar() throws SQLException {

        System.out.println("\n--- BAJA EJEMPLAR ---");

        // Se solicita el ISBN del libro
        System.out.print("ISBN: ");
        String isbn = sc.nextLine();

        // Se solicita el código del ejemplar concreto
        System.out.print("Código del ejemplar: ");
        int cod = Integer.parseInt(sc.nextLine());

        // Comprobamos que el ejemplar exista
        if (!ejemplarDAO.existeEjemplar(isbn, cod)) {
            System.out.println("El ejemplar no existe.");
            return;
        }

        // Se obtiene el estado actual del ejemplar
        EstadoEjemplar estado = ejemplarDAO.obtenerEstadoEjemplar(isbn, cod);

        // Cambio de estado → baja lógica
        ejemplarDAO.cambiarEstadoEjemplar(isbn, cod, EstadoEjemplar.DESCATALOGADO);

        // Confirmación al usuario
        System.out.println("Ejemplar dado de baja correctamente.");
    }

    // RF-1.4 Registrar incidencia
    private void registrarIncidencia() throws SQLException {
        
        System.out.println("\n--- REGISTRAR INCIDENCIA ---");

        System.out.print("ISBN: "); String isbn = sc.nextLine();
        System.out.print("Codigo ejemplar: "); int cod = Integer.parseInt(sc.nextLine());

        if (!ejemplarDAO.existeEjemplar(isbn, cod)) {
            System.out.println("El ejemplar no existe.");
            return;
        }

        EstadoEjemplar estado = ejemplarDAO.obtenerEstadoEjemplar(isbn, cod);
        if (estado == EstadoEjemplar.DESCATALOGADO) {
            System.out.println("No se puede registrar incidencia sobre un ejemplar descatalogado.");
            return;
        }

        int idInc = incidenciaDAO.generarIdIncidencia();
        System.out.print("Descripcion: "); String desc = sc.nextLine();
        System.out.print("Prioridad (1-5, opcional): "); String p = sc.nextLine();

        Integer prioridad = p.isEmpty() ? null : Integer.parseInt(p);

        IncidenciaEjemplar inc = new IncidenciaEjemplar(
                idInc, isbn, cod, new Date(), desc, prioridad, null
        );

        incidenciaDAO.insertarIncidencia(inc);
        ejemplarDAO.cambiarEstadoEjemplar(isbn, cod, EstadoEjemplar.NO_DISPONIBLE);

        System.out.println("Incidencia registrada con ID: " + idInc);
    }

    // RF-1.5 Listar libros con todos sus datos y ejemplares
    private void listarLibros() throws SQLException {

        System.out.println("\n--- LISTADO DE LIBROS ---\n");

        // Obtenemos todos los libros almacenados en la BD
        List<Libro> libros = libroDAO.listarLibros();

        // Cabecera de la tabla
        System.out.printf(
            "%-15s %-30s %-20s %-8s %-12s %-8s %-15s %-10s %-8s %-15s%n",
            "ISBN", "TÍTULO", "AUTOR",
            "TOTAL", "DISPONIBLES",
            "EDIC.", "EDITORIAL", "FECHA", "PÁGS", "GÉNERO"
        );

        // Línea separadora
        System.out.println(
            "--------------------------------------------------------------------------------------------------------------------------------"
        );

        // Recorremos todos los libros
        for (Libro l : libros) {

            //Contamos ejemplares totales y disponibles
            int total = ejemplarDAO.contarEjemplares(l.getIsbn());
            int disponibles = ejemplarDAO.contarEjemplaresDisponibles(l.getIsbn());

            // Para los valores opcionales  cambiamos null por  "-"
            String edicion = (l.getEdicion() != null) ? l.getEdicion().toString() : "-";
            String editorial = (l.getEditorial() != null) ? l.getEditorial() : "-";
            String genero = (l.getGenero() != null) ? l.getGenero() : "-";
            String paginas = (l.getNumPaginas() != null) ? l.getNumPaginas().toString() : "-";

            //Formateo de la fecha si existe
            String fecha = "-";
            if (l.getFechaPublicacion() != null) {
                fecha = new SimpleDateFormat("dd/MM/yyyy").format(l.getFechaPublicacion());
            }

            // Mostramos una fila completa de la tabla
            System.out.printf(
                "%-15s %-30s %-20s %-8d %-12d %-8s %-15s %-10s %-8s %-15s%n",
                l.getIsbn(),
                l.getTitulo(),
                l.getAutor(),
                total,
                disponibles,
                edicion,
                editorial,
                fecha,
                paginas,
                genero
            );
        }
    }

}
