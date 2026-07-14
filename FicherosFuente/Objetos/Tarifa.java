/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package practicapeajes.Objetos;

/**
 *
 * @author DavidArevaloRey
 */
import java.time.LocalDateTime;
/*
El objetivo de esta clase es usar la tarifa del sistema de peahe respecto
a la hora de salida y el tamanyo del vehiculo

*/
public class Tarifa {
    public static double calcular(Vehiculo vehiculo, LocalDateTime horaSalida){
        int hora = horaSalida.getHour();//obtiene la hora del dia
        double tamano = vehiculo.getTamano();
        boolean tramo1 = hora >= 0 && hora <= 11;
        if(tamano < 10) return tramo1 ? 2.5 : 3.0;
        if(tamano < 20) return tramo1 ? 4.0 : 5.0;
        return tramo1 ? 6.5 : 8.0;
    }
}
/*
Dependiendo de la hora especificara si el tramo es true o false,
si es false setaremos hablakndo del segundo tramo
Dependiendo del tramo calcula el tamnyo del vehiculo

*/