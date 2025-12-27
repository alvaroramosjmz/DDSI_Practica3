/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reservas;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class GestionReservas {
    
    private final ReservaLibroDAO dao; // Recibe el DAO directamente
    private final Scanner sc;


    public GestionReservas(ReservaLibroDAO dao) {
        this.dao = dao;
        this.sc = new Scanner(System.in); //sirve para leer lo que el usuario escribe
    }

    public void mostrarMenu() {
        int opcion = -1;
        do {
            System.out.println("\n===== SUBSISTEMA 3: RESERVA DE LIBROS =====");
            System.out.println("1. Realizar Reserva ");
            System.out.println("2. Cancelar Reserva ");
            System.out.println("3. Comprobar Reserva / Retirar ");
            System.out.println("4. Devolver Libro ");
            System.out.println("5. Historial de Usuario ");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");

            try {
                String input = sc.nextLine(); //leemos la linea como texto y luego lo pasamos a numero
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            try {
                switch (opcion) {
                    case 1 -> { if (avisoDatosPersonales()) realizarReserva(); }
                    case 2 -> cancelarReserva();
                    case 3 -> { if (avisoDatosPersonales()) comprobarReserva(); }
                    case 4 -> devolverLibro();
                    case 5 -> { if (avisoDatosPersonales()) historialUsuario(); }
                    case 0 -> {}
                    default -> System.out.println("Opción no válida.");
                }
            } catch (SQLException ex) {
                System.out.println("ERROR SQL: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }

        } while (opcion != 0);
    }

    // --- MÉTODOS PRIVADOS (Lógica UI) ---

    private void realizarReserva() throws SQLException {
        System.out.print("Introduzca ISBN: ");
        String isbn = sc.nextLine();
        System.out.print("Introduzca ID Usuario: ");
        try {
            int uid = Integer.parseInt(sc.nextLine());
            System.out.println(dao.realizarReserva(isbn, uid));
        } catch (NumberFormatException e) { System.out.println("ID debe ser numérico."); }
    }

    private void cancelarReserva() throws SQLException {
        System.out.print("Introduzca ID Usuario: ");
        try {
            int uid = Integer.parseInt(sc.nextLine());
            System.out.print("Introduzca ISBN a cancelar: ");
            String isbn = sc.nextLine();
            System.out.println(dao.cancelarReserva(uid, isbn));
        } catch (NumberFormatException e) { System.out.println("ID debe ser numérico."); }
    }

    private void comprobarReserva() throws SQLException {
        System.out.print("Introduzca ID Usuario: ");
        try {
            int uid = Integer.parseInt(sc.nextLine());
            System.out.print("Introduzca ISBN a retirar: ");
            String isbn = sc.nextLine();
            if (dao.tieneReservaActiva(uid, isbn)) {
                System.out.println(">> OK: Reserva válida. Puede entregar el libro.");
            } else {
                System.out.println(">> DENEGADO: No tiene reserva activa.");
            }
        } catch (NumberFormatException e) { System.out.println("ID debe ser numérico."); }
    }

    private void devolverLibro() throws SQLException {
        System.out.print("Introduzca ID Usuario: ");
        try {
            int uid = Integer.parseInt(sc.nextLine());
            System.out.print("Introduzca ISBN devuelto: ");
            String isbn = sc.nextLine();
            System.out.println(dao.devolverLibro(uid, isbn));
        } catch (NumberFormatException e) { System.out.println("ID debe ser numérico."); }
    }

    private void historialUsuario() throws SQLException {
        System.out.print("Introduzca ID Usuario: ");
        try {
            int uid = Integer.parseInt(sc.nextLine());
            List<ReservaLibro> lista = dao.obtenerHistorial(uid);
            if (lista.isEmpty()) {
                System.out.println("No hay reservas.");
            } else {
                System.out.println("--- HISTORIAL ---");
                for (ReservaLibro r : lista) {
                    String estado = r.isReservaValida() ? "ACTIVA" : "FINALIZADA";
                    System.out.println("Fecha: " + r.getFechaReserva() + " | Libro: " + r.getIsbn() + 
                                       " | Ejemplar: " + r.getCodEjemplar() + " | Estado: " + estado);
                }
            }
        } catch (NumberFormatException e) { System.out.println("ID debe ser numérico."); }
    }

    //politica de privacidad
    private boolean avisoDatosPersonales() {
        System.out.println("\n[AVISO LEGAL] Operación con datos personales.");
        System.out.print("¿Consiente el tratamiento? (S/N): ");
        String s = sc.nextLine();
        return s != null && s.trim().equalsIgnoreCase("S"); //compruebo que s no sea nulo y elimino espacios con trim, comparo para ver si es verdadero ya sea S o s
    }
}