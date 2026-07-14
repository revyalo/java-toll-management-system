/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package practicapeajes.Escaneres;

/**
 *
 * @author DavidArevaloRey
 */
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import practicapeajes.Enumeraciones.TipoRadar;
import practicapeajes.Objetos.Multa;
import practicapeajes.Objetos.Vehiculo;

/*
El objetivo de esta clase es implementar un radar que pueda registrar un coche en caso de 
que sobrepase la velocidad permitida y genere una multa, calculando la melocidad media
del trayecto
*/

public class RadarTramo {
    private static final double DISTANCIA = 100.0; //esta variable estatica des la distancia
    private static final double BORDE = 120.00;

    public static double calcularImporte(double v) {
        if(v <= 130) return 150;
        if(v <= 140) return 300;
        if(v <= 150) return 700;
        return 1500;
    }//devuelve el precio a pagar respecto a la velocidad

    public static Multa calcularMulta(Vehiculo v){
        LocalDateTime e = v.getEntrada();
        LocalDateTime s = v.getSalida();//obtendremos la fecha de salida y entrada del vehiculo

        if(e == null || s==null){
            return null;
        }
        double segundos = Duration.between(e, s).getSeconds();
        if(segundos <= 0){
            return null;
        }
        double velocidadMedia = DISTANCIA / (segundos / 3600.0);
        if(velocidadMedia <= BORDE){ return null;}
        double importeFinal = calcularImporte(velocidadMedia);
        return new Multa(v.getMatricula(), s, velocidadMedia, importeFinal, TipoRadar.TRAMO);
    }
}