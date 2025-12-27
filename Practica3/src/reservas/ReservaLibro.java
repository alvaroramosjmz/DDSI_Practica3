/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

   package reservas;

   import java.sql.Date;

   //definicion de la clase y atributos
    public class ReservaLibro {
        private int reservaId; //identificador
        private String isbn;
        private int codEjemplar;
        private int usuarioId;
        private Date fechaReserva;
        private boolean reservaValida;
        
        //constructor por defecto
        public ReservaLibro() {
        }

        public ReservaLibro(int reservaId, String isbn, int codEjemplar, int usuarioId, Date fechaReserva, boolean reservaValida) {
            this.reservaId = reservaId;
            this.isbn = isbn;
            this.codEjemplar = codEjemplar;
            this.usuarioId = usuarioId;
            this.fechaReserva = fechaReserva;
            this.reservaValida = reservaValida;
        }

        // Getters y Setters
        public int getReservaId() { return reservaId; }
        public void setReservaId(int reservaId) { this.reservaId = reservaId; }

        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }

        public int getCodEjemplar() { return codEjemplar; }
        public void setCodEjemplar(int codEjemplar) { this.codEjemplar = codEjemplar; }

        public int getUsuarioId() { return usuarioId; }
        public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

        public Date getFechaReserva() { return fechaReserva; }
        public void setFechaReserva(Date fechaReserva) { this.fechaReserva = fechaReserva; }

        public boolean isReservaValida() { return reservaValida; }
        public void setReservaValida(boolean reservaValida) { this.reservaValida = reservaValida; }

        @Override
        public String toString() {
            return "ReservaLibro{" + "ID=" + reservaId + ", ISBN=" + isbn + 
                   ", Ejemplar=" + codEjemplar + ", Usuario=" + usuarioId + 
                   ", Fecha=" + fechaReserva + ", Valida=" + reservaValida + '}';
        }
    }