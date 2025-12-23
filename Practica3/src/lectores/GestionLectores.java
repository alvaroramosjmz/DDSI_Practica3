/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lectores;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class GestionLectores {
    private LectorDAO lectorDAO;
    private Scanner sc;

    public GestionLectores(LectorDAO dao) {
        this.lectorDAO = dao;
        this.sc = new Scanner(System.in);
    }

    public void mostrarMenu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n--- GESTIÓN DE LECTORES ---");
            System.out.println("1. Alta Lector (2.1)");
            System.out.println("2. Modificar Lector (2.2)");
            System.out.println("3. Baja Lector (2.3)");
            System.out.println("4. Buscar Lector (2.4)");
            System.out.println("5. Listar Todos (2.5)");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            try {
                opcion = Integer.parseInt(sc.nextLine());
                switch (opcion) {
                    case 1 -> altaLector();
                    case 2 -> modificarLector();
                    case 3 -> bajaLector();
                    case 4 -> buscarLector();
                    case 5 -> listarLectores();
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void altaLector() throws SQLException {
        System.out.println("\nAVISO LOPD: Sus datos serán guardados en la BD de la Biblioteca.");
        System.out.print("Nombre: "); String n = sc.nextLine();
        System.out.print("Apellidos: "); String a = sc.nextLine();
        System.out.print("Telf: "); String t = sc.nextLine();
        lectorDAO.insertarLector(new Lector(0, n, a, t, "N"));
        System.out.println("Lector guardado.");
    }

    private void modificarLector() throws SQLException {
        System.out.print("ID a modificar: ");
        int id = Integer.parseInt(sc.nextLine());
        Lector l = lectorDAO.buscarLectorPorId(id);
        if (l != null) {
            System.out.print("Nuevo Nombre [" + l.getNombre() + "]: "); String n = sc.nextLine();
            if (!n.isEmpty()) l.setNombre(n);
            System.out.print("Nuevo Telf [" + l.getTelefono() + "]: "); String t = sc.nextLine();
            if (!t.isEmpty()) l.setTelefono(t);
            System.out.print("¿Malicioso? (S/N): "); String m = sc.nextLine();
            if (!m.isEmpty()) l.setEsMalicioso(m.toUpperCase());
            
            lectorDAO.modificarLector(l);
            System.out.println("Lector actualizado.");
        } else {
            System.out.println("No existe ese ID.");
        }
    }

    private void bajaLector() throws SQLException {
        System.out.print("ID a eliminar: ");
        int id = Integer.parseInt(sc.nextLine());
        lectorDAO.eliminarLector(id);
        System.out.println("Eliminado.");
    }

    private void buscarLector() throws SQLException {
        System.out.print("ID a buscar: ");
        int id = Integer.parseInt(sc.nextLine());
        Lector l = lectorDAO.buscarLectorPorId(id);
        if (l != null) {
            System.out.println("Datos: " + l.getNombre() + " " + l.getApellidos() + " | Telf: " + l.getTelefono());
        } else {
            System.out.println("Lector no encontrado.");
        }
    }

    private void listarLectores() throws SQLException {
        ArrayList<Lector> lista = lectorDAO.listarLectores();
        System.out.println("\nID | Nombre | Apellidos | Telf | Malicioso");
        for (Lector l : lista) {
            System.out.println(l.getId() + " | " + l.getNombre() + " | " + l.getApellidos() + " | " + l.getTelefono() + " | " + l.getEsMalicioso());
        }
    }
}