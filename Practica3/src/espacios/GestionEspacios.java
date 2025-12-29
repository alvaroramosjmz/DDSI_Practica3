/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package espacios;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author uSer
 */
public class GestionEspacios {
    
    private final EspaciosDAO dao;
    private final Scanner sc;

    // Formato recomendado para que el usuario introduzca fechas
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public GestionEspacios(EspaciosDAO dao) {
        this.dao = dao;
        this.sc = new Scanner(System.in);
    }
    
    
    public void mostrarMenu() {
        int opcion;

        do {
            System.out.println("\n===== SUBSISTEMA 4: GESTION DE ESPACIOS =====");
            System.out.println("1. Alta Espacio");
            System.out.println("2. Listar Espacios");
            System.out.println("3. Nueva Reserva");
            System.out.println("4. Cancelar Reserva");
            System.out.println("5. Registrar incidencia");
            System.out.println("6. Listar Reserva");
            System.out.println("0. Volver");

            opcion = leerEntero("Seleccione una opcion: ");

            try {
                switch (opcion) {
                    case 1 -> altaEspacio();
                    case 2 -> listarEspacios();
                    case 3 -> nuevaReserva();
                    case 4 -> cancelarReserva();
                    case 5 -> registrarIncidencia();
                    case 6 -> listarReservas();
                    case 0 -> System.out.println("Volviendo al menu principal...");
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (SQLException e) {
                // Mensaje útil: si viene de trigger, ya suele venir muy claro
                System.out.println("[ERROR BD] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
            }

        } while (opcion != 0);
    }
    
     /* =====================================================
       1) Alta Espacio
       ===================================================== */
    private void altaEspacio() throws SQLException {
        System.out.println("\n--- Alta Espacio ---");

        String nombre = leerTextoNoVacio("Nombre: ");
        String tipo = leerTextoNoVacio("Tipo: ");
        int capacidad = leerEnteroMin("Capacidad (>=0): ", 0);

        String estado = leerEstadoEspacio("Estado (BLOQUEADO / NO BLOQUEADO) [NO BLOQUEADO]: ", "NO BLOQUEADO");

        Espacio e = new Espacio(0, nombre, tipo, capacidad, estado);
        dao.insertarEspacio(e);

        System.out.println("[OK] Espacio dado de alta correctamente.");
    }

    /* =====================================================
       2) Listar Espacios (RF-4.1)
       ===================================================== */
    private void listarEspacios() throws SQLException {
        System.out.println("\n--- Listar Espacios ---");

        String estado = leerTextoOpcional("Filtrar por estado (BLOQUEADO / NO BLOQUEADO) [Enter = sin filtro]: ");
        if (estado != null && !estado.isBlank()) estado = estado.toUpperCase();
        else estado = null;

        String tipo = leerTextoOpcional("Filtrar por tipo [Enter = sin filtro]: ");
        if (tipo != null && !tipo.isBlank()) tipo = tipo.toUpperCase();
        else tipo = null;

        String capStr = leerTextoOpcional("Capacidad minima (>=0) [Enter = sin filtro]: ");
        Integer capacidadMin = null;
        if (capStr != null && !capStr.isBlank()) {
            try {
                capacidadMin = Integer.parseInt(capStr.trim());
                if (capacidadMin < 0) {
                    System.out.println("Capacidad minima invalida. Se ignorara el filtro.");
                    capacidadMin = null;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Capacidad minima invalida. Se ignorara el filtro.");
            }
        }

        List<Espacio> lista = dao.listarEspacios(estado, tipo, capacidadMin);

        if (lista.isEmpty()) {
            System.out.println("No hay espacios que cumplan los filtros.");
            return;
        }

        System.out.println("\nESPACIOS:");
        for (Espacio e : lista) {
            System.out.println(" - " + e);
        }
    }

    /* =====================================================
       3) Nueva Reserva (RF-4.2)
       ===================================================== */
    private void nuevaReserva() throws SQLException {
        System.out.println("\n--- Nueva Reserva ---");

        if (!avisoDatosPersonales()) {
            System.out.println("Operacion cancelada por el usuario.");
            return;
        }

        int usuarioId = leerEnteroMin("Usuario ID: ", 1);
        int espacioId = leerEnteroMin("Espacio ID: ", 1);

        Timestamp inicio = leerTimestamp("Inicio (yyyy-MM-dd HH:mm): ");
        Timestamp fin = leerTimestamp("Fin    (yyyy-MM-dd HH:mm): ");

        int reservaId = dao.crearReserva(usuarioId, espacioId, inicio, fin);
        System.out.println("[OK] Reserva creada correctamente. ID de reserva: " + reservaId);
    }

    /* =====================================================
       4) Cancelar Reserva (RF-4.3)
       ===================================================== */
    private void cancelarReserva() throws SQLException {
        System.out.println("\n--- Cancelar Reserva ---");

        if (!avisoDatosPersonales()) {
            System.out.println("Operacion cancelada por el usuario.");
            return;
        }

        int reservaId = leerEnteroMin("Reserva ID: ", 1);
        dao.cancelarReservaPorId(reservaId);

        System.out.println("[OK] Reserva cancelada correctamente.");
    }

    /* =====================================================
       5) Registrar Incidencia (RF-4.4)
       ===================================================== */
    private void registrarIncidencia() throws SQLException {
        System.out.println("\n--- Registrar Incidencia ---");

        if (!avisoDatosPersonales()) {
            System.out.println("Operacion cancelada por el usuario.");
            return;
        }

        int espacioId = leerEnteroMin("Espacio ID: ", 1);
        String descripcion = leerTextoNoVacioMax("Descripcion (máx 500): ", 500);

        Timestamp inicioInc = leerTimestamp("Inicio incidencia (yyyy-MM-dd HH:mm): ");
        Timestamp finInc = leerTimestamp("Fin incidencia    (yyyy-MM-dd HH:mm): ");

        String accion = leerAccionReserva("Accion sobre reservas (MANTENER / CANCELAR_Y_AVISAR) [MANTENER]: ", "MANTENER");

        IncidenciaEspacio inc = new IncidenciaEspacio(
                0,
                espacioId,
                descripcion,
                inicioInc,
                finInc,
                accion
        );

        dao.registrarIncidencia(inc);
        System.out.println("[OK] Incidencia registrada. El espacio ha quedado BLOQUEADO.");
    }

    /* =====================================================
       6) Listar Reservas (RF-4.5)
       ===================================================== */
    private void listarReservas() throws SQLException {
        System.out.println("\n--- Listar Reservas ---");

        if (!avisoDatosPersonales()) {
            System.out.println("Operacion cancelada por el usuario.");
            return;
        }

        String espStr = leerTextoOpcional("Filtrar por Espacio ID [Enter = sin filtro]: ");
        Integer espacioId = null;
        if (espStr != null && !espStr.isBlank()) {
            try {
                espacioId = Integer.parseInt(espStr.trim());
                if (espacioId <= 0) espacioId = null;
            } catch (NumberFormatException e) {
                System.out.println("Espacio ID invalido. Se ignorara el filtro.");
            }
        }

        Timestamp inicio = null;
        Timestamp fin = null;

        String iniStr = leerTextoOpcional("Filtrar por Inicio >= (yyyy-MM-dd HH:mm) [Enter = sin filtro]: ");
        if (iniStr != null && !iniStr.isBlank()) {
            inicio = parseTimestamp(iniStr);
            if (inicio == null) {
                System.out.println("Formato de fecha invalido. Se ignorara filtro de inicio.");
            }
        }

        String finStr = leerTextoOpcional("Filtrar por Fin <= (yyyy-MM-dd HH:mm) [Enter = sin filtro]: ");
        if (finStr != null && !finStr.isBlank()) {
            fin = parseTimestamp(finStr);
            if (fin == null) {
                System.out.println("Formato de fecha invalido. Se ignorara filtro de fin.");
            }
        }

        if (inicio != null && fin != null && inicio.after(fin)) {
            System.out.println("Rango invalido: INICIO no puede ser mayor que FIN. Se ignora el rango.");
            inicio = null;
            fin = null;
        }

        String estado = leerTextoOpcional("Estado reserva (CONFIRMADA / CANCELADA / CANCELADA_INCIDENCIA) [Enter = sin filtro]: ");
        if (estado != null && !estado.isBlank()) estado = estado.toUpperCase();
        else estado = null;

        List<ReservaEspacio> lista = dao.listarReservas(espacioId, inicio, fin, estado);

        if (lista.isEmpty()) {
            System.out.println("No hay reservas que cumplan los filtros.");
            return;
        }

        System.out.println("\nRESERVAS:");
        for (ReservaEspacio r : lista) {
            System.out.println(" - " + r);
        }
    }

    /* =====================================================
       UTILIDADES DE ENTRADA
       ===================================================== */

    private int leerEntero(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine();
            try {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException e) {
                System.out.println("Debe introducir un numero entero.");
            }
        }
    }

    private int leerEnteroMin(String msg, int min) {
        while (true) {
            int val = leerEntero(msg);
            if (val >= min) return val;
            System.out.println("El valor debe ser >= " + min);
        }
    }

    private String leerTextoNoVacio(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine();
            if (s != null && !s.trim().isEmpty()) return s.trim();
            System.out.println("No puede estar vacio.");
        }
    }

    private String leerTextoNoVacioMax(String msg, int max) {
        while (true) {
            String s = leerTextoNoVacio(msg);
            if (s.length() <= max) return s;
            System.out.println("Demasiado largo (max " + max + " caracteres).");
        }
    }

    private String leerTextoOpcional(String msg) {
        System.out.print(msg);
        return sc.nextLine();
    }

    private Timestamp leerTimestamp(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine();
            Timestamp ts = parseTimestamp(s);
            if (ts != null) return ts;
            System.out.println("Formato invalido. Use: yyyy-MM-dd HH:mm (ej: 2025-12-24 18:30)");
        }
    }

    private Timestamp parseTimestamp(String s) {
        try {
            LocalDateTime ldt = LocalDateTime.parse(s.trim(), FMT);
            return Timestamp.valueOf(ldt);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String leerEstadoEspacio(String msg, String porDefecto) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine();
            if (s == null || s.trim().isEmpty()) return porDefecto;
            s = s.trim().toUpperCase();
            if (s.equals("BLOQUEADO") || s.equals("NO BLOQUEADO")) return s;
            System.out.println("Estado invalido. Valores validos: BLOQUEADO / NO BLOQUEADO");
        }
    }

    private String leerAccionReserva(String msg, String porDefecto) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine();
            if (s == null || s.trim().isEmpty()) return porDefecto;
            s = s.trim().toUpperCase();
            if (s.equals("MANTENER") || s.equals("CANCELAR_Y_AVISAR")) return s;
            System.out.println("Accion invalida. Valores validos: MANTENER / CANCELAR_Y_AVISAR");
        }
    }

    /* =====================================================
       AVISO DATOS SENSIBLES (Práctica 3)
       ===================================================== */
    private boolean avisoDatosPersonales() {
        System.out.println("\n[AVISO DE PROTECCIÓN DE DATOS]");
        System.out.println("Esta operacion puede tratar datos personales (UsuarioID asociado a un lector).");
        System.out.println("Finalidad: gestión de espacios, reservas e incidencias de la biblioteca.");
        System.out.println("Derechos: acceso, rectificación y supresión según normativa vigente.");
        System.out.print("¿Acepta continuar? (S/N): ");

        String s = sc.nextLine();
        return s != null && (s.trim().equalsIgnoreCase("S") || s.trim().equalsIgnoreCase("SI"));
    }
}
