package usuarioDeSistema;

import java.sql.*;

public class SeguridadDAO {
    private Connection conn;

    public SeguridadDAO(Connection conn) {
        this.conn = conn;
    }

    // --- RF-5.1: Crear Bibliotecario ---
    public void crearBibliotecario(String nombre, String email, String telefono) throws SQLException {
        // Validación básica de formato de teléfono (RF-5.1.2)
        if (!telefono.matches("\\+\\d{11}")) { // Ejemplo: +34 666 555 444 (12 chars total)
            throw new SQLException("El teléfono debe tener formato +yyxxxxxxxxx (ej: +34600111222)");
        }

        String sql = "INSERT INTO USUARIOS_SISTEMA (NOMBRE, EMAIL, TELEFONO, ES_ADMIN, ES_MALICIOSO) VALUES (?, ?, ?, 'N', 'N')";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, email);
            pstmt.setString(3, telefono);
            pstmt.executeUpdate();
            conn.commit();
        }
    }

    // --- RF-5.2: Registrar Acceso (Se llama desde Login) ---
    public void registrarAcceso(int usuarioId) {
        String sql = "INSERT INTO ACCESOS_SISTEMA (USUARIO_ID, FECHA_ACCESO) VALUES (?, CURRENT_TIMESTAMP)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error al registrar log de acceso: " + e.getMessage());
        }
    }

    // --- RF-5.3: Listar Accesos (Solo Admin) ---
    public void listarHistorialAccesos() throws SQLException {
        String sql = "SELECT h.FECHA_ACCESO, u.NOMBRE, u.EMAIL, u.ES_ADMIN " +
                     "FROM ACCESOS_SISTEMA h " +
                     "JOIN USUARIOS_SISTEMA u ON h.USUARIO_ID = u.ID " +
                     "ORDER BY h.FECHA_ACCESO DESC";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\n--- HISTORIAL DE ACCESOS AL SISTEMA ---");
            System.out.printf("%-25s %-20s %-25s %-15s\n", "FECHA", "NOMBRE", "EMAIL", "ROL");
            System.out.println("---------------------------------------------------------------------------------------");
            
            while (rs.next()) {
                String fecha = rs.getTimestamp("FECHA_ACCESO").toString();
                String nombre = rs.getString("NOMBRE");
                String email = rs.getString("EMAIL");
                String rol = rs.getString("ES_ADMIN").equals("S") ? "ADMIN" : "BIBLIOTECARIO";
                
                System.out.printf("%-25s %-20s %-25s %-15s\n", fecha, nombre, email, rol);
            }
        }
    }

    // --- RF-5.4: Marcar Staff Malicioso (Solo Admin) ---
    public void marcarStaffMalicioso(String emailStaff, boolean esMalicioso) throws SQLException {
        // Evitar autobloqueo
        String sqlCheck = "SELECT ES_ADMIN FROM USUARIOS_SISTEMA WHERE EMAIL = ?";
        try(PreparedStatement p = conn.prepareStatement(sqlCheck)){
            p.setString(1, emailStaff);
            ResultSet rs = p.executeQuery();
            if(rs.next() && rs.getString("ES_ADMIN").equals("S") && esMalicioso){
                // Opcional: Impedir bloquear a otros admins
                // throw new SQLException("No se puede bloquear a un administrador.");
            }
        }

        String sql = "UPDATE USUARIOS_SISTEMA SET ES_MALICIOSO = ? WHERE EMAIL = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, esMalicioso ? "S" : "N");
            pstmt.setString(2, emailStaff);
            int rows = pstmt.executeUpdate();
            if (rows == 0) throw new SQLException("Usuario no encontrado.");
            conn.commit();
        }
    }

    // --- RF-5.5: Marcar Lector Malicioso (Cualquier Staff) ---
    // Ataca a la tabla LECTORES
    public void marcarLectorMalicioso(int lectorId, boolean esMalicioso) throws SQLException {
        String sql = "UPDATE LECTORES SET ES_MALICIOSO = ? WHERE USUARIO_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, esMalicioso ? "S" : "N");
            pstmt.setInt(2, lectorId);
            int rows = pstmt.executeUpdate();
            if (rows == 0) throw new SQLException("Lector no encontrado con ID: " + lectorId);
            conn.commit();
        }
    }
}