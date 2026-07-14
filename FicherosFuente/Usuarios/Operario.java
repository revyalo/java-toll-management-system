/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicapeajes.Usuarios;

/**
 *
 * @author DavidArevaloRey
 */
import java.time.LocalDateTime;
import java.util.List;
import practicapeajes.Enumeraciones.TipoRadar;
import practicapeajes.Objetos.Multa;
import practicapeajes.Objetos.Ticket;
import practicapeajes.Objetos.Vehiculo;
import practicapeajes.Sistema.SistemaPeaje;

public class Operario {
    private SistemaPeaje sistema;

    public Operario(SistemaPeaje sistema) {
        this.sistema = sistema;
    }

    public boolean registrarEntrada(String matricula, LocalDateTime fecha) {
        Vehiculo coche = new Vehiculo(matricula, 0.0);
        boolean registrada = sistema.registrarEntrada(coche, fecha);
        System.out.println("Operario: Solicita registrar la entrada de " + matricula);
        return registrada;

    }

    public boolean registrarSalida(String matricula, LocalDateTime fecha, double tamano) {
        boolean registrada = sistema.registrarSalida(matricula, fecha, tamano);
     System.out.println("Operario: Solicita registrar la salida de " + matricula);
     return registrada;

    }
    
    public List<Multa> verMultasPagadas(String m){
        System.out.println("Operario: Solicita ver todas las multas pagadas de " + m);

        return sistema.obtenerMultasPagadas(m);
    }
    
    public void historialPeajes(String matricula){
    sistema.exportarHistorial(matricula, LocalDateTime.now());
    System.out.println("Operario: Solicita exportar el historial de peajes de la matricula" + matricula);

    }
    
    public List<Multa> verMultasPendientes(String matricula){
    System.out.println("Operario: Solicita ver todas las multas pendientes de " + matricula);

        return sistema.consultarMultasPendientes(matricula);
    }
    
    public List<Multa> verMultasPorTipo(String matricula, TipoRadar tipo){
    System.out.println("Operario: Solicita ver todas las multas de " + matricula);

        return sistema.consultarMultasPorTipo(matricula, tipo);
        
    }
    
    public List<Multa> verTodasMultas(String matricula){
    System.out.println("Operario: Solicita ver todas las multas de " + matricula);

        return sistema.consultarMultas(matricula);

    }
    
    public List<Ticket> verTickets(String matricula){
        return sistema.consultarTickets(matricula);
    
    }
    public void exportarHistorial(String matricula){
        sistema.exportarHistorial(matricula, LocalDateTime.now());
        System.out.println("Operario: Solicita exportar historial de " + matricula);
    }
    
    public boolean aplicarMultaMovil(String matricula, double velocidad, LocalDateTime fecha){
    boolean registrada = sistema.hacerMultaMovil(matricula, velocidad, fecha);
        System.out.println("Operario: Solicita aplicar multa movil de " + matricula);
        return registrada;
    
    }
    
    public Ticket generarTicket(String matricula){
        System.out.println("Operario: Solicita generar un ticket " + matricula);

        return sistema.generarTicket(matricula);

    }
    public Vehiculo modificarTamanoVehiculo(String m, double n){
         System.out.println("Operario: Solicita modificar un tamanyo de la siguiente matricula " + m);
        return sistema.modificarTamanoVehiculo( m,  n);
    
    }
    
    
    

}
