package ui;

import usuarioDeSistema.UsuarioDeSistemaDAO; // O el paquete correcto
import usuarioDeSistema.UsuarioDeSistema;
import usuarioDeSistema.SeguridadDAO; // IMPORTANTE
import java.sql.Connection;
import java.util.Scanner;

public class LoginMenu {
    
    public static UsuarioDeSistema autenticar(Connection conn) {
        Scanner sc = new Scanner(System.in);
        UsuarioDeSistemaDAO dao = new UsuarioDeSistemaDAO(conn);
        SeguridadDAO segDao = new SeguridadDAO(conn); // Para registrar el log
        
        System.out.println("\n=== LOGIN DE SISTEMA ===");
        System.out.print("Email: ");
        String email = sc.nextLine();

        try {
            UsuarioDeSistema user = dao.login(email);

            if (user == null) {
                System.err.println("Error: El usuario no existe.");
                return null;
            }

            if (user.esMalicioso()) {
                System.err.println("ACCESO DENEGADO: Su cuenta está bloqueada.");
                return null;
            }

            // --- RF-5.2: REGISTRO DE SESIÓN EXITOSA ---
            segDao.registrarAcceso(user.getId());
            // ------------------------------------------

            System.out.println("Bienvenido, " + user.getNombre());
            return user;

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        } 
    }
}