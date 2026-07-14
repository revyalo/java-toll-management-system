/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package practicapeajes.Escaneres;

/**
 *
 * @author DavidArevaloRey
 */
import practicapeajes.Escaneres.Camara;
import java.time.LocalDateTime;
import practicapeajes.Enumeraciones.TipoRadar;
import practicapeajes.Objetos.Multa;

/*
El objetivo de esta clase es implementar un radar que pueda registrar un coche en caso de 
que sobrepase la velocidad permitida en un punto exacto y genere una multa
*/

public class RadarMovil extends Camara {
    private String matricula;
    private double velocidad;
    private final static double EXCESO = 120.00; //esta variable estatica es el maximo de velocidad

    public void deteccionExceso(String matricula, double velocidad) {
        if(velocidad > EXCESO){
            this.velocidad = velocidad;
            this.matricula = matricula;
            iniciarTransmision();
        }
    }//si encuentra que el vehiculo sobre pasa la velocidad guarda al informacion
    public static double calcularImporte(double v) {
        if(v <= 130) return 100;
        if(v <= 140) return 150;
        if(v <= 150) return 230;
        return 350;
    }//dependiendo de la velocidad, retornara el precio de la multa

    public static Multa hacerMulta(String matricula, LocalDateTime fecha, double velocidad) {
        double importe = calcularImporte(velocidad);
        return new Multa(matricula, fecha, velocidad, importe, TipoRadar.MOVIL);

    }//generara la multa

    public String getMatricula() {
        return matricula;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public void transmitirDatos(){
        this.matricula = null;
        this.velocidad = 0;
        activar();
    }
}
