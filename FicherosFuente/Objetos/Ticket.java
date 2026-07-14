/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package practicapeajes.Objetos;

/**
 *
 * @author DavidArevaloRey
 */
import java.io.Serializable;
import practicapeajes.Objetos.Tarifa;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/*
El objetivo de esta clase es implementar un objeto
Ticket que se transfomara posteriomente en un archivo binario,
tendra las variables principales declardas y sobre todo la variable valor contenida
*/
public class Ticket implements Serializable  {
    private String matricula;
    private double tamano;
    private LocalDateTime fecha;
    private double valor;

    public Ticket(Vehiculo vehiculo) {
        this.matricula = vehiculo.getMatricula();
        this.tamano = vehiculo.getTamano();
        this.fecha = vehiculo.getSalida();
        this.valor = Tarifa.calcular(vehiculo, fecha);
    }
    public String getMatricula() {
        return matricula;
    }
    public double getTamano(){
        return tamano;
    }
    public LocalDateTime getFecha(){
    return fecha;
    }
    
    
    @Override
    public String toString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("Ticket - Matricula: %s, Tamanyo: %s, Fecha: %s, Tarifa: %.2f euros",
     matricula, tamano, fecha.format(formatter), valor);
    }
  

}
