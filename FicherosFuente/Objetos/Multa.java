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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import practicapeajes.Enumeraciones.TipoRadar;

/*
La clase multa tiene el objetivo de registrar en un lugar
los datos almacenados en una multa

Utilizaremos serializable para transformar los datos en binario
Emplearemos comparable para ordenar la lista de multas
*/

public class Multa implements Serializable, Comparable<Multa> {
    private static int contadorIds = 0;
    private String matricula;
    private LocalDateTime fecha;
    private double velocidad;
    private double importe;
    private boolean pagada;
    private TipoRadar tipo;
    private int identificador;
    public Multa(String matricula, LocalDateTime fecha, double velocidad, double importe, TipoRadar tipo) {
        this.identificador = ++contadorIds;
        this.matricula = matricula;
        this.fecha = fecha;
        this.velocidad = velocidad;
        this.importe = importe;
        this.pagada = false;
        this.tipo = tipo;
        
    }
    
    public Multa(String matricula, int id){
    this.identificador = id;
    this.matricula = matricula;
    }
    

    public TipoRadar getTipo(){
        return tipo;
    }
    
    public void pagar() {
        this.pagada = true;
    }

    public boolean isPagada() {
        return pagada;
    }
    public String getMatricula() {
        return matricula;
    }
    public LocalDateTime getFecha() {
        return fecha;
    }
    public double getVelocidad() {
        return velocidad;
    }
    public double getImporte() {
        return importe;
    }
    
    public int getId(){
     return identificador;
    }
    

    public int compareTo(Multa o) {
        if(this.pagada != o.pagada) {
            return this.pagada ? 1 : -1;
        }
        return this.fecha.compareTo(o.fecha);
    }
    /*
    Comparamos si las mutlas estan pagadas o no, el if pregunta si la multa actual
    tiene un estado de pago diferente al que se compara, si son igaules el if se salta y comparara
    mediente las fechas de multa, si this fecha es antes que o fecha devolvera un numero negativo
    al contrario deolvera uno positivo.
    En el interior del ifsi la muslta esta pafgada retornamos 1 al contrario retornaremos -1
    Entonces ordenara dentro del estado si pagads o no, y ordenara por fecha de forma ascendente
    
    */
    
    public boolean equals(Object o){
    if(this == o) return true;
    if(o == null) return false;
    if(getClass() != o.getClass()) return false;
    Multa m = (Multa) o;
    return this.identificador == m.identificador &&
            this.matricula.equalsIgnoreCase(m.matricula);
    
    }
    
    public String toString(){
    return String.format("Multa: %s, Matricula: %s, Fecha: %s, Velocidad: %.3f km/h, Importe: %.2f, Pagada: %s, Identificador: %d",
    tipo, matricula, fecha, velocidad, importe, pagada ? "SI" : "NO", identificador);
//si la multa esta pagada pondra un SI o NO dependiendo en el toString
    
    }
}

