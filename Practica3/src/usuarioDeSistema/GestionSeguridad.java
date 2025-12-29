package usuarioDeSistema;

import database.DBConnection;
import lectores.Lector;
import lectores.LectorDAO;

import java.util.ArrayList;
import java.util.Scanner;

public class GestionSeguridad {
    private static Scanner sc = new Scanner(System.in);

    public static void mostrarMenu(UsuarioDeSistema usuario) {
        SeguridadDAO dao = new SeguridadDAO(DBConnection.getConnection());
        LectorDAO lectorDAO = new LectorDAO(DBConnection.getConnection());
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n=== GESTIÓN DE SEGURIDAD ===");
            
            // Opciones SOLO para ADMINISTRADORES
            if (usuario.esAdmin()) {
                System.out.println("1. [ADMIN] Crear nuevo Bibliotecario (RF-5.1)");
                System.out.println("2. [ADMIN] Ver Historial de Accesos (RF-5.3)");
                System.out.println("3. [ADMIN] Gestionar Staff Malicioso (RF-5.4)");
            }
            
            // Opciones para TODOS (Admin y Bibliotecarios)
            System.out.println("4. Bloquear/Desbloquear Lector (RF-5.5)");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione opción: ");

            try {
                String input = sc.nextLine();
                if(input.isEmpty()) continue;
                opcion = Integer.parseInt(input);

                switch (opcion) {
                    case 1 -> {
                        if (!usuario.esAdmin()) { System.out.println("Acceso Denegado."); break; }
                        System.out.print("Nombre completo: ");
                        String nombre = sc.nextLine();
                        System.out.print("Email: ");
                        String email = sc.nextLine();
                        System.out.print("Teléfono (+yyxxxxxxxxx): ");
                        String tlf = sc.nextLine();
                        dao.crearBibliotecario(nombre, email, tlf);
                        System.out.println("[OK] Bibliotecario creado.");
                    }
                    case 2 -> {
                        if (!usuario.esAdmin()) { System.out.println("Acceso Denegado."); break; }
                        dao.listarHistorialAccesos();
                    }
                    case 3 -> {
                        if (!usuario.esAdmin()) { System.out.println("Acceso Denegado."); break; }
                        dao.listarUsuariosDeSistema();
                        System.out.print("Email del staff a bloquear/desbloquear: ");
                        String mail = sc.nextLine();
                        System.out.print("¿Es malicioso? (S/N): ");
                        boolean esMal = sc.nextLine().equalsIgnoreCase("S");
                        dao.marcarStaffMalicioso(mail, esMal);
                        System.out.println("[OK] Estado actualizado.");
                        dao.listarUsuariosDeSistema();
                    }
                    case 4 -> {
                        // RF-5.5: Disponible para Admin y Bibliotecario
                        // Primero, listamos los lectores para que el usuario pueda elegir
                        listarLectores(lectorDAO);
                        System.out.print("ID del Lector: ");
                        int idLector = Integer.parseInt(sc.nextLine());
                        System.out.print("¿Marcar como malicioso (Bloquear)? (S/N): ");
                        boolean block = sc.nextLine().equalsIgnoreCase("S");
                        dao.marcarLectorMalicioso(idLector, block);
                        System.out.println("[OK] Estado del lector actualizado.");
                        listarLectores(lectorDAO);
                    }
                    case 0 -> System.out.println("Volviendo...");
                    default -> System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    public static void listarLectores(LectorDAO lectorDAO) {
        try {
            ArrayList<Lector> lista = lectorDAO.listarLectores();
            System.out.println("\nID | Nombre | Apellidos | Telf | Malicioso");
            for (Lector l : lista) {
                System.out.println(l.getId() + " | " + l.getNombre() + " | " + l.getApellidos() + " | " + l.getTelefono() + " | " + l.getEsMalicioso());
            }
        } catch (Exception e) {
            System.out.println("[ERROR] No se pudieron listar los lectores: " + e.getMessage());
        }
    }
}