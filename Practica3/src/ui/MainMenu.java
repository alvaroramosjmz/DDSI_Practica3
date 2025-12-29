/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import database.DBConnection;
import database.TableManagerLectores;
import database.TableManagerEspacios;
import database.TableManagerLibros;
import database.TableManagerUsuariosDeSistema;
import database.TableManagerReservas;
import libros.LibroDAO;
import libros.EjemplarDAO;
import libros.IncidenciaEjemplarDAO;
import libros.GestionLibros;
import lectores.LectorDAO;
import lectores.GestionLectores;
import espacios.EspaciosDAO;
import espacios.GestionEspacios;
import reservas.GestionReservas;
import reservas.ReservaLibroDAO;
import usuarioDeSistema.GestionSeguridad;
import usuarioDeSistema.UsuarioDeSistema;
import java.sql.Connection;
import java.util.Scanner;

public class MainMenu {
    private static Scanner sc = new Scanner(System.in);

    public static void mostrar(UsuarioDeSistema usuarioLogueado) {
        Connection conn = DBConnection.getConnection();
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n********** SISTEMA DE GESTION BIBLIOTECARIA **********");
            System.out.println("   Usuario: " + usuarioLogueado.getEmail() + (usuarioLogueado.esAdmin() ? " [ADMIN]" : ""));
            System.out.println("------------------------------------------------------");
            System.out.println("1. GESTION DE LIBROS");
            System.out.println("2. GESTION DE LECTORES");
            System.out.println("3. GESTION DE RESERVAS");
            System.out.println("4. GESTION DE ESPACIOS");
            System.out.println("5. GESTION DE SEGURIDAD");

            System.out.println("------------------------------------------------------");
            System.out.println("9. INICIALIZAR/BORRAR TABLAS (Mantenimiento)");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());

                switch (opcion) {
                    case 1 -> {
                        try{
                            LibroDAO libroDAO = new LibroDAO(conn);
                            EjemplarDAO ejemplarDAO = new EjemplarDAO(conn);
                            IncidenciaEjemplarDAO incidenciaDAO = new IncidenciaEjemplarDAO(conn);
                            GestionLibros gestionLibros = new GestionLibros(libroDAO, ejemplarDAO, incidenciaDAO);
                            gestionLibros.mostrarMenu();
                        } catch (Exception e) {
                            System.out.println("[ERROR] No se pudo iniciar el subsistema de espacios: " + e.getMessage());
                        }
                        
                    }  

                    
                    case 2 -> {
                        // ENTRADA LIMPIA: Solo cargamos el menu de lectores
                        LectorDAO dao = new LectorDAO(conn);
                        GestionLectores gestion = new GestionLectores(dao);
                        gestion.mostrarMenu();
                    }
                    
                    case 3 -> {
                        try {                          
                            reservas.ReservaLibroDAO reservaDAO = new reservas.ReservaLibroDAO(conn);
                            reservas.GestionReservas gestionReservas = new reservas.GestionReservas(reservaDAO);
                            gestionReservas.mostrarMenu();

                        } catch (Exception e) {
                            System.out.println("[ERROR] Fallo al abrir Reservas: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    case 4 -> {
                        try {
                            EspaciosDAO espaciosDAO = new EspaciosDAO(conn);
                            GestionEspacios gestionEspacios = new GestionEspacios(espaciosDAO);
                            gestionEspacios.mostrarMenu();
                        } catch (Exception e) {
                            System.out.println("[ERROR] No se pudo iniciar el subsistema de espacios: " + e.getMessage());
                        }
                    }
                    case 5 -> {
                        GestionSeguridad.mostrarMenu(usuarioLogueado);
                    }
                    
                    case 9 -> {
                        // Esta es la opcion para cuando quieras limpiar todo y empezar de cero
                        System.out.println("CUIDADO! Se borraran todos los datos.");
                        System.out.print("Esta seguro? (S/N): ");
                        if (sc.nextLine().equalsIgnoreCase("S")) {
                            try {
                                System.out.println("\n[MANTENIMIENTO BD]");
                                
                                // 1) Libros y ejemplares
                                TableManagerLibros tmLibros = new TableManagerLibros(conn);
                                tmLibros.crearEstructuraLibros();

                                
                                // 2) Lectores
                                TableManagerLectores tmLectores = new TableManagerLectores(conn);
                                tmLectores.crearEstructuraLectores();

                                // 3) Espacios
                                TableManagerEspacios tmEspacios = new TableManagerEspacios(conn);
                                tmEspacios.crearEstructuraEspacios();

                                // 4) Usuarios de Sistema
                                TableManagerUsuariosDeSistema tmUsuarios = new TableManagerUsuariosDeSistema(conn);
                                tmUsuarios.crearEstructuraUsuarios();
                                
                                // 5) Reservas
                                TableManagerReservas tmReservas = new TableManagerReservas(conn);
                                tmReservas.crearEstructuraReservas();

                                conn.commit();
                                System.out.println("[OK] Estructura de la base de datos creada correctamente.");

                            } catch (Exception e) {
                                try {
                                    conn.rollback();
                                } catch (Exception ignored) {}

                                System.out.println("[ERROR] Fallo en el mantenimiento de la BD: " + e.getMessage());
                            }
                        }
                    }
                    
                    case 0 -> System.out.println("Cerrando programa...");
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
