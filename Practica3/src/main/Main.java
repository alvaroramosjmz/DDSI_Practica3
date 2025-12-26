
import database.DBConnection;
import ui.MainMenu; // Importante para que encuentre el menú de los 5 puntos
import ui.LoginMenu; // Importante para que encuentre el menú de login
import usuarioDeSistema.UsuarioDeSistema;
import database.TableManagerUsuariosDeSistema;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        // 1. Intentar conectar
        Connection conn = DBConnection.getConnection();
        // try {
        //     new TableManagerUsuariosDeSistema(conn).crearEstructuraUsuarios();
        //     conn.commit();
        // } catch(Exception e) { e.printStackTrace(); }

        if (conn != null) {

            UsuarioDeSistema usuarioLogueado = LoginMenu.autenticar(DBConnection.getConnection());

            if (usuarioLogueado == null) {
                System.err.println("Error: No se pudo autenticar el usuario. Saliendo...");
                DBConnection.closeConnection();
                return;
            }
            // 2. LANZAR EL MENÚ PRINCIPAL (El de los 5 puntos)
            MainMenu.mostrar(usuarioLogueado);
            
            // 3. Al salir, cerrar
            DBConnection.closeConnection();
            
        } else {
            System.err.println("Error: No se pudo conectar a la base de datos.");
        }
    }
}