/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package practicapeajes.Objetos;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author DavidArevaloRey
 */

/*
El objetivo de esta clase es implementar un vehiculo con sus cualidades como amtricula y tamanyo
Se implementara la interfaz serializable para transmutarlo en un archivo binario en el futuro
*/
public class Vehiculo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String matricula;
    private double tamano;
    private LocalDateTime entrada;
    private LocalDateTime salida;

    public Vehiculo(String matricula, double tamano) {
        this.matricula = matricula;
        this.tamano = tamano;
    }
    public String getMatricula() {
        return matricula;
    }
    public double getTamano() {
        return tamano;
    }
    public LocalDateTime getEntrada() {
        return entrada;
    }
    public LocalDateTime getSalida() {
        return salida;
    }

    public void setEntrada(LocalDateTime entrada) {
        this.entrada = entrada;
    }
    public void setSalida(LocalDateTime salida) {
        this.salida = salida;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    public void setTamano(double tamano) {
        this.tamano = tamano;
    }
}
