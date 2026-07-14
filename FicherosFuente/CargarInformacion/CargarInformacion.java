/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicapeajes.Ficheros;

import java.time.LocalDateTime;
import practicapeajes.Datos.Datos;
import practicapeajes.Enumeraciones.TipoRadar;
import practicapeajes.Objetos.Multa;
import practicapeajes.Objetos.Vehiculo;
import practicapeajes.Sistema.SistemaPeaje;

/**
 *
 * @author DavidArevaloRey
 */
/*
El objetivo de esta clase es la carga de datos del sistema para usarlo posteriomente con las
clases descritas en todo el programa y sobre todo aplicarlo en la interfaz

*/


public class CargarInformacion {
    public static void main(String[] args){
        SistemaPeaje sistema = new SistemaPeaje();

        Vehiculo v1 = new Vehiculo("1234ABC", 8.0);
        Vehiculo v2 = new Vehiculo("5678DEF", 15.0);
        Vehiculo v3 = new Vehiculo("9012GHI", 22.0);
        Vehiculo v4 = new Vehiculo("3456JKL", 9.5);
        Vehiculo v5 = new Vehiculo("7890MNO", 18.0);
        Vehiculo v6 = new Vehiculo("2468PQR", 12.0);
        Vehiculo v7 = new Vehiculo("1357STU", 25.0);
        Vehiculo v8 = new Vehiculo("8642VWX", 7.5);

        sistema.registrarEntrada(v1, LocalDateTime.of(2025, 5, 20, 9, 0));
        sistema.registrarEntrada(v2, LocalDateTime.of(2025, 5, 20, 9, 15));
        sistema.registrarEntrada(v3, LocalDateTime.of(2025, 5, 20, 9, 30));
        sistema.registrarEntrada(v4, LocalDateTime.of(2025, 5, 20, 9, 45));
        sistema.registrarEntrada(v5, LocalDateTime.of(2025, 5, 20, 10, 0));
        sistema.registrarEntrada(v6, LocalDateTime.of(2025, 5, 20, 10, 10));
        sistema.registrarEntrada(v7, LocalDateTime.of(2025, 5, 20, 10, 20));
        sistema.registrarEntrada(v8, LocalDateTime.of(2025, 5, 20, 10, 25));

        sistema.registrarSalida("1234ABC", LocalDateTime.of(2025, 5, 20, 9, 45), 8.0);
        sistema.registrarSalida("5678DEF", LocalDateTime.of(2025, 5, 20, 10, 0), 15.0);
        sistema.registrarSalida("9012GHI", LocalDateTime.of(2025, 5, 20, 10, 15), 22.0);
        sistema.registrarSalida("3456JKL", LocalDateTime.of(2025, 5, 20, 10, 30), 9.5);
        sistema.registrarSalida("7890MNO", LocalDateTime.of(2025, 5, 20, 10, 45), 18.0);
        sistema.registrarSalida("2468PQR", LocalDateTime.of(2025, 5, 20, 10, 50), 12.0);
        sistema.registrarSalida("1357STU", LocalDateTime.of(2025, 5, 20, 11, 0), 25.0);
        sistema.registrarSalida("8642VWX", LocalDateTime.of(2025, 5, 20, 11, 5), 7.5);

        sistema.registrarEntrada(v1, LocalDateTime.of(2025, 5, 21, 11, 0));
        sistema.registrarEntrada(v2, LocalDateTime.of(2025, 5, 21, 11, 15));
        sistema.registrarEntrada(v3, LocalDateTime.of(2025, 5, 21, 11, 30));
        sistema.registrarEntrada(v4, LocalDateTime.of(2025, 5, 21, 11, 45));
        sistema.registrarEntrada(v5, LocalDateTime.of(2025, 5, 21, 12, 0));
        sistema.registrarEntrada(v6, LocalDateTime.of(2025, 5, 21, 12, 10));
        sistema.registrarEntrada(v7, LocalDateTime.of(2025, 5, 21, 12, 15));
        sistema.registrarEntrada(v8, LocalDateTime.of(2025, 5, 21, 12, 20));

        sistema.registrarSalida("1234ABC", LocalDateTime.of(2025, 5, 21, 11, 45), 8.0);
        sistema.registrarSalida("5678DEF", LocalDateTime.of(2025, 5, 21, 12, 0), 15.0);
        sistema.registrarSalida("9012GHI", LocalDateTime.of(2025, 5, 21, 12, 15), 22.0);
        sistema.registrarSalida("3456JKL", LocalDateTime.of(2025, 5, 21, 12, 30), 9.5);
        sistema.registrarSalida("7890MNO", LocalDateTime.of(2025, 5, 21, 12, 45), 18.0);
        sistema.registrarSalida("2468PQR", LocalDateTime.of(2025, 5, 21, 12, 50), 12.0);
        sistema.registrarSalida("1357STU", LocalDateTime.of(2025, 5, 21, 12, 55), 25.0);
        sistema.registrarSalida("8642VWX", LocalDateTime.of(2025, 5, 21, 13, 0), 7.5);

        sistema.hacerMultaMovil("5678DEF", 135, LocalDateTime.of(2025, 5, 20, 10, 0));   
        sistema.hacerMultaMovil("9012GHI", 160, LocalDateTime.of(2025, 5, 20, 10, 15));  
        sistema.hacerMultaMovil("7890MNO", 140, LocalDateTime.of(2025, 5, 21, 12, 45));  
        sistema.hacerMultaMovil("2468PQR", 155, LocalDateTime.of(2025, 5, 21, 12, 50));  
        sistema.hacerMultaMovil("1357STU", 130, LocalDateTime.of(2025, 5, 21, 12, 55));  


        sistema.registrarMulta(
            new Multa("1234ABC", LocalDateTime.of(2025, 5, 20, 9, 0), 125.0, 300.0, TipoRadar.TRAMO));    
        sistema.registrarMulta(
            new Multa("3456JKL", LocalDateTime.of(2025, 5, 21, 11, 45), 155.0, 1500.0, TipoRadar.TRAMO));  

        Multa multaPagada = new Multa("9999ZZZ", LocalDateTime.of(2025, 5, 21, 13, 0), 145.0, 200.0, TipoRadar.TRAMO);
        multaPagada.pagar(); 
        sistema.registrarMulta(multaPagada);

        Multa multaPagada2 = new Multa("5678DEF", LocalDateTime.of(2025, 5, 21, 12, 0), 135.0, 300.0, TipoRadar.TRAMO);
        multaPagada2.pagar(); 
        sistema.registrarMulta(multaPagada2);
        
        
        
        Vehiculo v9 = new Vehiculo("1122BBB", 13.5);
        Vehiculo v10 = new Vehiculo("3344CCC", 28.0);
        Vehiculo v11 = new Vehiculo("5566DDD", 9.8);
        Vehiculo v12 = new Vehiculo("7788EEE", 16.2);
        Vehiculo v13 = new Vehiculo("9900FFF", 7.1);

        sistema.registrarEntrada(v9, LocalDateTime.of(2025, 5, 22, 8, 5));
        sistema.registrarEntrada(v10, LocalDateTime.of(2025, 5, 22, 8, 20));
        sistema.registrarEntrada(v1, LocalDateTime.of(2025, 5, 22, 8, 30));
        sistema.registrarEntrada(v11, LocalDateTime.of(2025, 5, 22, 9, 0));
        sistema.registrarEntrada(v4, LocalDateTime.of(2025, 5, 22, 9, 15));

        sistema.registrarSalida("1122BBB", LocalDateTime.of(2025, 5, 22, 8, 55), 13.5);
        sistema.registrarSalida("3344CCC", LocalDateTime.of(2025, 5, 22, 9, 10), 28.0);
        sistema.registrarSalida("1234ABC", LocalDateTime.of(2025, 5, 22, 9, 40), 8.0);
        sistema.registrarSalida("5566DDD", LocalDateTime.of(2025, 5, 22, 9, 45), 9.8);
        sistema.registrarSalida("3456JKL", LocalDateTime.of(2025, 5, 22, 10, 5), 9.5);

        sistema.registrarEntrada(v12, LocalDateTime.of(2025, 5, 22, 15, 0));
        sistema.registrarEntrada(v13, LocalDateTime.of(2025, 5, 22, 15, 30));
        sistema.registrarEntrada(v2, LocalDateTime.of(2025, 5, 22, 16, 0));

        sistema.registrarSalida("7788EEE", LocalDateTime.of(2025, 5, 22, 16, 15), 16.2);
        sistema.registrarSalida("9900FFF", LocalDateTime.of(2025, 5, 22, 16, 45), 7.1);
        sistema.registrarSalida("5678DEF", LocalDateTime.of(2025, 5, 22, 17, 0), 15.0);

        sistema.registrarEntrada(v5, LocalDateTime.of(2025, 5, 22, 22, 0));
        sistema.registrarSalida("7890MNO", LocalDateTime.of(2025, 5, 23, 7, 30), 18.0);

        sistema.registrarEntrada(v8, LocalDateTime.of(2025, 5, 23, 9, 5));
        sistema.registrarEntrada(v9, LocalDateTime.of(2025, 5, 23, 9, 20));
        sistema.registrarEntrada(v10, LocalDateTime.of(2025, 5, 23, 9, 35));

        sistema.registrarSalida("8642VWX", LocalDateTime.of(2025, 5, 23, 10, 0), 7.5);
        sistema.registrarSalida("1122BBB", LocalDateTime.of(2025, 5, 23, 10, 30), 13.5);

        sistema.hacerMultaMovil("1122BBB", 145, LocalDateTime.of(2025, 5, 22, 8, 40));
        sistema.hacerMultaMovil("3344CCC", 130, LocalDateTime.of(2025, 5, 22, 9, 0));
        sistema.hacerMultaMovil("1234ABC", 165, LocalDateTime.of(2025, 5, 22, 9, 15));
        sistema.hacerMultaMovil("7788EEE", 150, LocalDateTime.of(2025, 5, 22, 15, 45));
        sistema.hacerMultaMovil("8642VWX", 125, LocalDateTime.of(2025, 5, 23, 9, 30));

        sistema.registrarMulta(
            new Multa("9900FFF", LocalDateTime.of(2025, 5, 22, 15, 30), 135.0, 400.0, TipoRadar.TRAMO));
        sistema.registrarMulta(
            new Multa("7890MNO", LocalDateTime.of(2025, 5, 22, 22, 0), 142.0, 550.0, TipoRadar.TRAMO));
        sistema.registrarMulta(
            new Multa("1122BBB", LocalDateTime.of(2025, 5, 23, 9, 20), 158.0, 1600.0, TipoRadar.TRAMO));

        Multa multaPagada3 = new Multa("5566DDD", LocalDateTime.of(2025, 5, 22, 9, 45), 121.0, 100.0, TipoRadar.TRAMO);
        multaPagada3.pagar();
        sistema.registrarMulta(multaPagada3);

        Multa multaPagada4 = new Multa("9012GHI", LocalDateTime.of(2025, 5, 21, 12, 15), 140.0, 300.0, TipoRadar.TRAMO);
        multaPagada4.pagar();
        sistema.registrarMulta(multaPagada4);

        Multa multaExternaPagada = new Multa("0000XYZ", LocalDateTime.of(2025, 5, 23, 14, 0), 130.0, 150.0, TipoRadar.TRAMO);
        multaExternaPagada.pagar();
        sistema.registrarMulta(multaExternaPagada);

        Multa multaMovilPagada = new Multa("2468PQR", LocalDateTime.of(2025, 5, 21, 12, 50), 155.0, 450.0, TipoRadar.MOVIL);
        multaMovilPagada.pagar();
        sistema.registrarMulta(multaMovilPagada);

        Vehiculo v14 = new Vehiculo("2233GGG", 19.1);
        sistema.registrarEntrada(v14, LocalDateTime.of(2025, 5, 24, 10, 0));
        sistema.registrarSalida("2233GGG", LocalDateTime.of(2025, 5, 24, 11, 15), 19.1);
        sistema.hacerMultaMovil("2233GGG", 180, LocalDateTime.of(2025, 5, 24, 10, 50));
        sistema.registrarMulta(
            new Multa("3456JKL", LocalDateTime.of(2025, 5, 22, 9, 15), 148.0, 700.0, TipoRadar.TRAMO));
        sistema.registrarEntrada(v7, LocalDateTime.of(2025, 5, 24, 12, 00));
        Multa multaPagada5 = new Multa("1357STU", LocalDateTime.of(2025, 5, 24, 12, 00), 128.0, 100.0, TipoRadar.MOVIL);
        multaPagada5.pagar();
        sistema.registrarMulta(multaPagada5);



        Datos.guardarDatos(sistema);

        System.out.println("Datos de prueba generados y guardados.");



}
    
}
