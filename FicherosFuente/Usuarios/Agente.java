/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicapeajes.Usuarios;

import java.time.LocalDateTime;
import practicapeajes.Sistema.SistemaPeaje;

/**
 *
 * @author DavidArevaloRey
 */
public class Agente {
    private SistemaPeaje sistema;
    
    public Agente(SistemaPeaje sistema){
    this.sistema = sistema;
    }
    
    public boolean multarCoches(String matricula, double velocidad){
        boolean registrada = sistema.hacerMultaMovil(matricula, velocidad, LocalDateTime.now());
        System.out.println("Agente: Solicita multas a " + matricula + "por la velocidad: " + velocidad + "km/hora");
        return registrada;

    }
    
    
}
