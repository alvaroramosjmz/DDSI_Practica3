/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import database.DBConnection;

import java.sql.Connection;
import java.util.Scanner;

/**
 *
 * @author Usuario
 */
public class MainMenu {
    
    public static void mostrar() throws Exception {
        Connection connection = DBConnection.getConnection();
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("===== MENU PRINCIPAL =====");
            System.out.println("1. Crear tablas e insertar datos iniciales");
            System.out.println("2. Dar de alta nuevo pedido");
            System.out.println("3. Mostrar contenido de las tablas");
            System.out.println("4. Salir");
            System.out.print("Elige una opcion: ");
            opcion = sc.nextInt();

        } while (opcion != 4);
    }
}
