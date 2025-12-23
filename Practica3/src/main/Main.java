/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import ui.MainMenu;

/**
 *
 * @author Usuario
 */
public class Main {
        public static void main(String[] args) {
        try {
            MainMenu.mostrar();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
