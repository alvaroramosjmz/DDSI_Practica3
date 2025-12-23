/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;// conexión activa con la DB
import java.sql.DriverManager; //busca driver JDBC (en nuestro caso de Oracle) y abre la conection
import java.sql.SQLException; //// para lanzar excepciones cuando ocurren errores SQL o de conexion

/**
 *
 * @author Usuario
 */
public class DBConnection {
    
    // Variable tipo Connection static para que toda la app comparta una unica conexion a la BD 
    private static Connection connection = null; 
    
    // Método para obtener la conexion
    public static Connection getConnection(){
        
        // Comprobamos previamente si no hay una conexión abierta. Si la hay, devuelve la misma
        if(connection == null){
            try{
                
                String nombre_servidor = "oracle0.ugr.es";
                String num_puerto = "1521";
                String service_name = "practbd";
                
                // Definimos la URL JDBC de conexion
                String url = "jdbc:oracle:thin:@//" + nombre_servidor + ":" + num_puerto + "/" + service_name;
            
                // Credenciales de acceso a Oracle
                String user = "x7034010";
                String password = "x7034010";
                
                // Crea la conexión --> busca un driver que comprenda la URL y abre una sesión en la BD 
                connection = DriverManager.getConnection(url, user, password);
                
                // Desactiva el modo autocommit pues ahora necesitamos controlar manualmente las transacciones
                connection.setAutoCommit(false);
                
                // Mensaje de confiramacion por consola
                System.out.println("Conexion establecida correctamente");
            } catch(SQLException e){
                System.err.println("Error al conectar a la BD: " + e.getMessage());
            }
        }
        
        return connection;
    }
    
    
    // Método para cerrar la conexión
    public static void closeConnection(){
        
        try{
            
            // Comprobamos previamente que la conexión exista y si está abierta antes de cerrarla
            if(connection != null & !connection.isClosed()){
                connection.close();
                System.out.println("Conexion cerrada correctamente");
            }
        } catch (SQLException e){
            System.err.println("Error al cerrar la conexion: " + e.getMessage());
        }
    }
    
    
    
    
}

