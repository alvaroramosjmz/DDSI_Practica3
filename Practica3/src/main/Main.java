
import database.DBConnection;
import ui.MainMenu; // Importante para que encuentre el menú de los 5 puntos

public class Main {
    public static void main(String[] args) {
        // 1. Intentar conectar
        if (DBConnection.getConnection() != null) {
            
            // 2. LANZAR EL MENÚ PRINCIPAL (El de los 5 puntos)
            MainMenu.mostrar();
            
            // 3. Al salir, cerrar
            DBConnection.closeConnection();
            
        } else {
            System.err.println("Error: No se pudo conectar a la base de datos.");
        }
    }
}