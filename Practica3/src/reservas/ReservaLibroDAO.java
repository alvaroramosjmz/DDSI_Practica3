/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reservas;

import libros.EstadoEjemplar; // Importamos el ENUM 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaLibroDAO {
    
    private Connection connection;
    
    //recibimos la conexion y la guardamos para poder usarla en los metodos
    public ReservaLibroDAO(Connection connection) {
        this.connection = connection;
    }
    
    //METODO AUXILIAR, VERIFICAR SI EXISTE EL USUARIO 
    private boolean existeUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT 1 FROM LECTORES WHERE USUARIO_ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }
    
    
    //////////////////////////////
    // RF-3.1: REALIZAR RESERVA//
    /////////////////////////////
    public String realizarReserva(String isbn, int usuarioId) throws SQLException {
        
        //consultas SQL
        String mensaje = "";
        PreparedStatement psBuscar = null;
        PreparedStatement psInsertar = null;
        PreparedStatement psUpdateEjemplar = null;
        PreparedStatement psCheckEstado = null;
        PreparedStatement psCheckUsuario = null;
        //resultados de SELECT
        ResultSet rs = null;
        ResultSet rsUsuario = null;

        try {
            //desactivamos el commit automatico, si algo falla hacemos rollback
            connection.setAutoCommit(false); 
            
            //Comprobamos si el usuario es malicioso
            String sqlUsuario = "SELECT ES_MALICIOSO FROM LECTORES WHERE USUARIO_ID = ?";
            psCheckUsuario = connection.prepareStatement(sqlUsuario);
            psCheckUsuario.setInt(1, usuarioId);
            rsUsuario = psCheckUsuario.executeQuery();

            if (rsUsuario.next()) {
                String esMalicioso = rsUsuario.getString("ES_MALICIOSO");
                // Si en la BD pone 'S', es que es malicioso
                if ("S".equals(esMalicioso)) {
                    // Cancelamos todo y devolvemos error
                    connection.rollback();
                    return "ERROR: El usuario está marcado como MALICIOSO y no puede reservar libros.";
                }
            } else {
                // Si no entra en el if, es que el usuario no existe en la BD
                connection.rollback();
                return "ERROR: El usuario con ID " + usuarioId + " no existe.";
            }

            // Intentar buscar el primer ejemplar que este DISPONIBLE de ese ISBN 
            String sqlBuscar = "SELECT CodEjemplar FROM EJEMPLAR WHERE ISBN = ? AND Estado = ? FETCH FIRST 1 ROWS ONLY";
            psBuscar = connection.prepareStatement(sqlBuscar); //buscamos en la base de datos
            psBuscar.setString(1, isbn); //buscamos el ejemplar con el isbn que se le pasó
            psBuscar.setString(2, EstadoEjemplar.DISPONIBLE.name()); // Usamos el Enum porque estamos buscando un ejemplar que este disponible
            rs = psBuscar.executeQuery(); //ejecutamos select y nos devuelve el resultado, que lo guardamos en ResultSet

            if (rs.next()) { //hay al menos una fila
                // Si hay un ejemplar disponible, leo el codigo del ejemplar
                int codEjemplar = rs.getInt("CodEjemplar");

                //Primero creamos la reserva
                String sqlInsertar = "INSERT INTO RESERVAS_LIBROS (ISBN, COD_EJEMPLAR, USUARIO_ID, FECHA_RESERVA, RESERVA_VALIDA) VALUES (?, ?, ?, SYSDATE, 'T')";
                psInsertar = connection.prepareStatement(sqlInsertar);
                psInsertar.setString(1, isbn);
                psInsertar.setInt(2, codEjemplar);
                psInsertar.setInt(3, usuarioId);
                psInsertar.executeUpdate(); //guardamos la reserva nueva

                // Cambiamos el estado del ejemplar 
                String sqlUpdate = "UPDATE EJEMPLAR SET Estado = ? WHERE ISBN = ? AND CodEjemplar = ?"; //este ejemplar ya no puede reservarse
                psUpdateEjemplar = connection.prepareStatement(sqlUpdate); 
                psUpdateEjemplar.setString(1, EstadoEjemplar.NO_DISPONIBLE.name());
                psUpdateEjemplar.setString(2, isbn);
                psUpdateEjemplar.setInt(3, codEjemplar);
                psUpdateEjemplar.executeUpdate();

                connection.commit(); //guardamos la reserva
                mensaje = "ÉXITO: Reserva realizada. Se ha reservado el ejemplar código: " + codEjemplar;
            
            } else {
                // Si no hay ejemplares disponibles, comprobamos POR QUÉ (Requisito usuario)
                connection.rollback(); //deshacemos cualquier cambio , no se toca nada en la base de datos
                
                //busca todos los ejemplares de ese isbn y quiero ver el estado de cada uno de ellos
                String sqlCheck = "SELECT Estado FROM EJEMPLAR WHERE ISBN = ?"; 
                psCheckEstado = connection.prepareStatement(sqlCheck); //ejecuto select
                psCheckEstado.setString(1, isbn);
                ResultSet rsEstado = psCheckEstado.executeQuery(); //guardo el resultado en rsEstado
                
                boolean hayDescatalogados = false; //saber si hay alguno descatalogado
                boolean hayOcupados = false; //saber si hay alguno no disponible
                boolean existeLibro = false; //saber si existe el libro o no
                    
                //voy comprobando la lista ejemplar a ejemplar de ese isbn

                while(rsEstado.next()){
                    existeLibro = true;
                    String estado = rsEstado.getString("Estado");

                    // PROTECCIÓN CONTRA NULOS Y ESPACIOS
                    if (estado != null) {
                        estado = estado.trim(); // 1. Quitamos espacios sobrantes "DESCATALOGADO " -> "DESCATALOGADO"

                        // 2. Comparamos ignorando mayúsculas/minúsculas
                        if (estado.equalsIgnoreCase(EstadoEjemplar.DESCATALOGADO.name())) {
                            hayDescatalogados = true;
                        }

                        if (estado.equalsIgnoreCase(EstadoEjemplar.NO_DISPONIBLE.name())) {
                            hayOcupados = true;
                        }
                    }
                }
                rsEstado.close();
                
                if (!existeLibro) {
                    mensaje = "ERROR: No existen ejemplares registrados para el ISBN " + isbn;
                } else if (hayDescatalogados && !hayOcupados) {
                    mensaje = "ERROR: No se puede reservar. Los ejemplares están DESCATALOGADOS.";
                } else {
                    mensaje = "ERROR: No se puede reservar. Todos los ejemplares están NO DISPONIBLES (Reservados/Prestados).";
                }
            }
            
            //si salta algun error en la base de datos entra en el catch
        } catch (SQLException e) {
            if (connection != null) connection.rollback(); //hacemos rollback para no quedarnos a medias y eliminamos todos los cambios hechos hasta ese momento
            throw e;
        } finally { //Siempre ejecutamos esto, cerramos todo
            if (connection != null) connection.setAutoCommit(true);
            if (rs != null) rs.close(); 
            if (psBuscar != null) psBuscar.close();
            if (psInsertar != null) psInsertar.close();
            if (psUpdateEjemplar != null) psUpdateEjemplar.close();
            if (psCheckEstado != null) psCheckEstado.close();
        }
        return mensaje;
    }
    
    ////////////////////////////////
    // RF-3.2: CANCELAR RESERVA  //
    ///////////////////////////////
    public String cancelarReserva(int usuarioId, String isbn) throws SQLException {
        
        String mensaje = "";
        PreparedStatement psCheck = null;
        PreparedStatement psUpdateReserva = null;
        PreparedStatement psUpdateEjemplar = null;
        ResultSet rs = null;

        try {
            connection.setAutoCommit(false);
            
            //comprobamos si el usuario existe
            if (!existeUsuario(usuarioId)) {
                connection.rollback();
                return "ERROR: El usuario con ID " + usuarioId + " no existe.";
            }

            String sqlCheck = "SELECT RESERVA_VALIDA, COD_EJEMPLAR, ES_RETIRADO FROM RESERVAS_LIBROS " +
                              "WHERE USUARIO_ID = ? AND ISBN = ? " +
                              "ORDER BY FECHA_RESERVA DESC FETCH FIRST 1 ROWS ONLY";

            psCheck = connection.prepareStatement(sqlCheck);
            psCheck.setInt(1, usuarioId);
            psCheck.setString(2, isbn);
            rs = psCheck.executeQuery();

            if (rs.next()) {
                String esValida = rs.getString("RESERVA_VALIDA");
                int codEjemplar = rs.getInt("COD_EJEMPLAR");
                String esRetirado = rs.getString("ES_RETIRADO"); 

                // Ya estaba finalizada la reserva
                if ("F".equals(esValida)) {
                    connection.rollback();
                    return "ERROR: No se puede cancelar. La reserva ya estaba FINALIZADA.";
                }

                // Está activa ('T'), pero lo ha retirado
                if ("S".equals(esRetirado)) {
                    connection.rollback();
                    return "ERROR: No puede CANCELAR un libro que ya ha RETIRADO. Debe usar la opción 'Devolver Libro'.";
                }

                // La reserva es valida y no se ha retirado, podemos cancelarla
                
                // 1. Ponemos la reserva a 'F' (False)
                String sqlUpdateRes = "UPDATE RESERVAS_LIBROS SET RESERVA_VALIDA = 'F' " +
                                      "WHERE USUARIO_ID = ? AND ISBN = ? AND COD_EJEMPLAR = ?";
                psUpdateReserva = connection.prepareStatement(sqlUpdateRes);
                psUpdateReserva.setInt(1, usuarioId);
                psUpdateReserva.setString(2, isbn);
                psUpdateReserva.setInt(3, codEjemplar);
                psUpdateReserva.executeUpdate();

                // 2. Liberamos el ejemplar (lo ponemos DISPONIBLE)
                String sqlUpdateEjem = "UPDATE EJEMPLAR SET Estado = ? WHERE ISBN = ? AND CodEjemplar = ?";
                psUpdateEjemplar = connection.prepareStatement(sqlUpdateEjem);
                psUpdateEjemplar.setString(1, EstadoEjemplar.DISPONIBLE.name());
                psUpdateEjemplar.setString(2, isbn);
                psUpdateEjemplar.setInt(3, codEjemplar);
                psUpdateEjemplar.executeUpdate();

                connection.commit();
                mensaje = "ÉXITO: Reserva cancelada correctamente. El ejemplar " + codEjemplar + " vuelve a estar libre.";

            } else {
                connection.rollback();
                return "ERROR: Este usuario no tiene reservas registradas para este libro.";
            }
        } catch (SQLException e) {
                if (connection != null) connection.rollback();
                throw e;
            } finally {
                if (connection != null) connection.setAutoCommit(true);
                if (rs != null) rs.close();
                if (psCheck != null) psCheck.close();
                if (psUpdateReserva != null) psUpdateReserva.close();
                if (psUpdateEjemplar != null) psUpdateEjemplar.close();
            }
            return mensaje;
            
    }

    
    //////////////////////////////
    //RF-3.3: RETIRAR LIBRO     //
    /////////////////////////////
    
    public String confirmarRetirada(int usuarioId, String isbn, int codEjemplar) throws SQLException {
        String mensaje = "";
        PreparedStatement psCheck = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;
        
        try {
            connection.setAutoCommit(false);

            //comprobamos si el usuario existe
            if (!existeUsuario(usuarioId)) {
                connection.rollback();
                return "ERROR: El usuario con ID " + usuarioId + " no existe en el sistema.";
            }

            // Ahora buscamos tambien por COD_EJEMPLAR
            String sqlCheck = "SELECT RESERVA_VALIDA, ES_RETIRADO FROM RESERVAS_LIBROS " +
                              "WHERE USUARIO_ID = ? AND ISBN = ? AND COD_EJEMPLAR = ?";
            
            psCheck = connection.prepareStatement(sqlCheck);
            psCheck.setInt(1, usuarioId);
            psCheck.setString(2, isbn);
            psCheck.setInt(3, codEjemplar); // Buscamos el ejemplar exacto
            
            rs = psCheck.executeQuery();

            if (rs.next()) {
                String esValida = rs.getString("RESERVA_VALIDA");
                String esRetirado = rs.getString("ES_RETIRADO");

                // CASO A: Reserva finalizada/cancelada
                if ("F".equals(esValida)) {
                    connection.rollback();
                    return "ERROR: No se puede retirar. La reserva de este ejemplar consta como FINALIZADA.";
                }
                
                // CASO B: Ya retirado
                if ("S".equals(esRetirado)) {
                     connection.rollback();
                     return "ERROR: Este ejemplar concreto YA HA SIDO RETIRADO previamente.";
                }

                // CASO C: Todo correcto. HACEMOS EL UPDATE.
                String sqlUpdate = "UPDATE RESERVAS_LIBROS SET ES_RETIRADO = 'S' " +
                                   "WHERE USUARIO_ID = ? AND ISBN = ? AND COD_EJEMPLAR = ?";
                
                psUpdate = connection.prepareStatement(sqlUpdate);
                psUpdate.setInt(1, usuarioId);
                psUpdate.setString(2, isbn);
                psUpdate.setInt(3, codEjemplar);
                psUpdate.executeUpdate();
                
                connection.commit();
                mensaje = "ÉXITO: Libro retirado. Ejemplar " + codEjemplar + " entregado al usuario.";

            } else {
                // Si no entra en el if, es que no coincide la reserva con ese ejemplar
                connection.rollback();
                return "ERROR: El usuario " + usuarioId + " NO tiene reservado el ejemplar " + codEjemplar + ". Compruebe si tiene reservada otra copia.";
            }

        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            throw e;
        } finally {
            if (connection != null) connection.setAutoCommit(true);
            if (rs != null) rs.close();
            if (psCheck != null) psCheck.close();
            if (psUpdate != null) psUpdate.close();
        }
        return mensaje;
    }
    
    ////////////////////////////////
    // RF-3.4: DEVOLVER LIBRO     //
    ///////////////////////////////
    public String devolverLibro(int usuarioId, String isbn, int codEjemplar) throws SQLException {
        
        String mensaje = "";
        PreparedStatement psCheck = null;
        PreparedStatement psUpdateReserva = null;
        PreparedStatement psUpdateEjemplar = null;
        ResultSet rs = null;

        try {
            connection.setAutoCommit(false);
            
            //comprobamos si el usuario existe 
            if (!existeUsuario(usuarioId)) {
                connection.rollback();
                return "ERROR: El usuario con ID " + usuarioId + " no existe.";
            }

            // Buscamos si tiene reservado ESE ejemplar concreto y si está retirado
            String sqlCheck = "SELECT RESERVA_VALIDA,ES_RETIRADO FROM RESERVAS_LIBROS " +
                              "WHERE USUARIO_ID = ? AND ISBN = ? AND COD_EJEMPLAR = ? ";
            
            psCheck = connection.prepareStatement(sqlCheck);
            psCheck.setInt(1, usuarioId);
            psCheck.setString(2, isbn);
            psCheck.setInt(3, codEjemplar); 
            
            rs = psCheck.executeQuery();
            
            if (rs.next()) {
                String esValida = rs.getString("RESERVA_VALIDA");
                String esRetirado = rs.getString("ES_RETIRADO");
                
                // Si está finalizada ('F') -> Significa que YA se devolvió
                if ("F".equals(esValida)) {
                     connection.rollback();
                     return "ERROR: Este libro ya ha sido DEVUELTO (Reserva finalizada).";
                }
                
                //Si no se ha retirado , no se puede devolver
                if ("N".equals(esRetirado)) {
                     connection.rollback();
                     return "ERROR: Ese ejemplar consta como NO RETIRADO. Use Cancelar Reserva.";
                }

                // 2. ACTUALIZAR RESERVA (Cerrarla)
                String sqlRes = "UPDATE RESERVAS_LIBROS SET RESERVA_VALIDA = 'F' " +
                                "WHERE USUARIO_ID = ? AND ISBN = ? AND COD_EJEMPLAR = ?";
                psUpdateReserva = connection.prepareStatement(sqlRes);
                psUpdateReserva.setInt(1, usuarioId);
                psUpdateReserva.setString(2, isbn);
                psUpdateReserva.setInt(3, codEjemplar);
                psUpdateReserva.executeUpdate();

                // 3. LIBERAR EJEMPLAR (Ponerlo Disponible)
                String sqlEjem = "UPDATE EJEMPLAR SET Estado = ? WHERE ISBN = ? AND CodEjemplar = ?";
                psUpdateEjemplar = connection.prepareStatement(sqlEjem);
                psUpdateEjemplar.setString(1, EstadoEjemplar.DISPONIBLE.name());
                psUpdateEjemplar.setString(2, isbn);
                psUpdateEjemplar.setInt(3, codEjemplar);
                psUpdateEjemplar.executeUpdate();

                connection.commit();
                mensaje = "ÉXITO: Libro devuelto. El Ejemplar " + codEjemplar + " vuelve a estar DISPONIBLE.";
            
            } else {
                connection.rollback();
                return "ERROR: No consta que este usuario tenga el Ejemplar " + codEjemplar + " de este libro.";
            }

        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            throw e;
        } finally {
            if (connection != null) connection.setAutoCommit(true);
            if (rs != null) rs.close();
            if (psCheck != null) psCheck.close();
            if (psUpdateReserva != null) psUpdateReserva.close();
            if (psUpdateEjemplar != null) psUpdateEjemplar.close();
        }
        return mensaje;
    }

    // Método auxiliar común
    private String finalizarReservaGenerico(int usuarioId, String isbn, String msgExito) throws SQLException {
        String mensaje = "";
        PreparedStatement psBuscar = null;
        PreparedStatement psUpdateReserva = null;
        PreparedStatement psUpdateEjemplar = null;
        ResultSet rs = null;

        try {
            connection.setAutoCommit(false); //lo ponemos a false para que si algo falla, no guarde nada

            // Buscar reserva activa de ese usuario con el isbn del libro que habia reservado,  necesitamos saber que ejemplar se llevo
            String sqlBuscar = "SELECT COD_EJEMPLAR FROM RESERVAS_LIBROS WHERE USUARIO_ID = ? AND ISBN = ? AND RESERVA_VALIDA = 'T'";
            psBuscar = connection.prepareStatement(sqlBuscar);
            psBuscar.setInt(1, usuarioId);
            psBuscar.setString(2, isbn);
            rs = psBuscar.executeQuery();

            if (rs.next()) { //si encontramos la reserva , guardamos el cod del ejemplar que se llevo
                int codEjemplar = rs.getInt("COD_EJEMPLAR");

                // Cambiamos la reserva para que no sea valida porque el libro ya se ha devuelto
                String sqlRes = "UPDATE RESERVAS_LIBROS SET RESERVA_VALIDA = 'F' WHERE USUARIO_ID = ? AND ISBN = ? AND COD_EJEMPLAR = ?";
                psUpdateReserva = connection.prepareStatement(sqlRes);
                psUpdateReserva.setInt(1, usuarioId);
                psUpdateReserva.setString(2, isbn);
                psUpdateReserva.setInt(3, codEjemplar);
                psUpdateReserva.executeUpdate();

                // Liberar ejemplar -> Ponerlo DISPONIBLE
                String sqlEjem = "UPDATE EJEMPLAR SET Estado = ? WHERE ISBN = ? AND CodEjemplar = ?";
                psUpdateEjemplar = connection.prepareStatement(sqlEjem);
                psUpdateEjemplar.setString(1, EstadoEjemplar.DISPONIBLE.name());
                psUpdateEjemplar.setString(2, isbn);
                psUpdateEjemplar.setInt(3, codEjemplar);
                psUpdateEjemplar.executeUpdate();

                connection.commit(); //todo ha ido bien, guardamos
                mensaje = "ÉXITO: " + msgExito + ". Ejemplar " + codEjemplar + " ahora está DISPONIBLE.";
            } else {
                mensaje = "ERROR: No existe reserva activa para este usuario y libro.";
            }

        } catch (SQLException e) {
            if (connection != null) connection.rollback();
            throw e;
        } finally {
            if (connection != null) connection.setAutoCommit(true);
            if (rs != null) rs.close();
            if (psBuscar != null) psBuscar.close();
            if (psUpdateReserva != null) psUpdateReserva.close();
            if (psUpdateEjemplar != null) psUpdateEjemplar.close();
        }
        return mensaje;
    }


    public boolean tieneReservaActiva(int usuarioId, String isbn) throws SQLException {
        //buscamos la reserva de ese libro y ese usuario y que sea valida
        String sql = "SELECT COUNT(*) FROM RESERVAS_LIBROS WHERE USUARIO_ID = ? AND ISBN = ? AND RESERVA_VALIDA = 'T'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) { //uso try y no tengo q poner close al final
            ps.setInt(1, usuarioId);
            ps.setString(2, isbn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0; //si devuelve 1, hay reserva a true, la reserva existe y se puede llevar el libro; de lo contrario no
        }
        return false;
    }

    /////////////////////////////
    //RF-3.5: HISTORIAL//
    ///////////////////////////// 
    public List<ReservaLibro> obtenerHistorial(int usuarioId) throws SQLException {
        List<ReservaLibro> lista = new ArrayList<>();
        //recupero todas las reservas de ese usuario, ordenado de fecha mas nueva a mas antigua
        String sql = "SELECT * FROM RESERVAS_LIBROS WHERE USUARIO_ID = ? ORDER BY FECHA_RESERVA DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { //si encontramos datos
                    boolean valida = "T".equals(rs.getString("RESERVA_VALIDA")); 
                    lista.add(new ReservaLibro(
                        rs.getInt("RESERVA_ID"),
                        rs.getString("ISBN"),
                        rs.getInt("COD_EJEMPLAR"),
                        rs.getInt("USUARIO_ID"),
                        rs.getDate("FECHA_RESERVA"),
                        valida
                    ));
                }
            }
        }
        return lista;
    }
}