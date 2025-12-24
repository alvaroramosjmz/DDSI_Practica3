/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import database.DBConnection;
import database.TableManagerLectores;
import database.TableManagerEspacios;
import lectores.LectorDAO;
import lectores.GestionLectores;
import espacios.EspaciosDAO;
import espacios.GestionEspacios;
import java.sql.Connection;
import java.util.Scanner;

public class MainMenu {
    private static Scanner sc = new Scanner(System.in);

    public static void mostrar() {
        Connection conn = DBConnection.getConnection();
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n********** SISTEMA DE GESTIÓN BIBLIOTECARIA **********");
            System.out.println("1. GESTIÓN DE LIBROS");
            System.out.println("2. GESTIÓN DE LECTORES");
            System.out.println("3. GESTIÓN DE RESERVAS");
            System.out.println("4. GESTIÓN DE ESPACIOS");
            System.out.println("5. GESTIÓN DE SEGURIDAD");
            System.out.println("------------------------------------------------------");
            System.out.println("9. INICIALIZAR/BORRAR TABLAS (Mantenimiento)");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());

                switch (opcion) {
                    case 1 -> System.out.println("[INFO] Subsistema 1: Libros (Pendiente).");
                    
                    case 2 -> {
                        // ENTRADA LIMPIA: Solo cargamos el menú de lectores
                        LectorDAO dao = new LectorDAO(conn);
                        GestionLectores gestion = new GestionLectores(dao);
                        gestion.mostrarMenu();
                    }
                    
                    case 3 -> System.out.println("[INFO] Subsistema 3: Reservas (Pendiente).");
                    case 4 -> {
                        try {
                            EspaciosDAO espaciosDAO = new EspaciosDAO(conn);
                            GestionEspacios gestionEspacios = new GestionEspacios(espaciosDAO);
                            gestionEspacios.mostrarMenu();
                        } catch (Exception e) {
                            System.out.println("[ERROR] No se pudo iniciar el subsistema de espacios: " + e.getMessage());
                        }
                    }
                    case 5 -> System.out.println("[INFO] Subsistema 5: Seguridad (Pendiente).");
                    
                    case 9 -> {
                        // Esta es la opción para cuando quieras limpiar todo y empezar de cero
                        System.out.println("¡CUIDADO! Se borrarán todos los datos de lectores.");
                        System.out.print("¿Estás seguro? (S/N): ");
                        if (sc.nextLine().equalsIgnoreCase("S")) {
                            try {
                                System.out.println("\n[MANTENIMIENTO BD]");

                                // 1) Lectores
                                TableManagerLectores tmLectores = new TableManagerLectores(conn);
                                tmLectores.crearEstructuraLectores();

                                // 2) Espacios
                                TableManagerEspacios tmEspacios = new TableManagerEspacios(conn);
                                tmEspacios.crearEstructuraEspacios();

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
                    default -> System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}