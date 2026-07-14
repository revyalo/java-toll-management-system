/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicapeajes.Usuarios;

import java.util.List;
import practicapeajes.Enumeraciones.TipoRadar;
import practicapeajes.Objetos.Multa;
import practicapeajes.Sistema.SistemaPeaje;

/**
 *
 * @author DavidArevaloRey
 */
public class Conductor {
    private SistemaPeaje sistema;
    public Conductor(SistemaPeaje sistema){
        this.sistema = sistema;
    }
    

      
      public boolean pagarMulta(String matricula, int id){
      System.out.println("Conductor: Solicita pagar la siguiente matricula " + matricula + " cuya identificacion: " + id);

      return sistema.pagarMulta(id, matricula);
      
      }
      
      public List<Multa> verMultas(String matricula){
          System.out.println("Conductor: Solicita ver todas las multas de la matricula " + matricula);
          return sistema.consultarMultas(matricula);
      }
      
      public List<Multa> verMultasPendientes(String matricula){
          System.out.println("Conductor: Solicita ver todas las multas pendientes de la matricula " + matricula);
          return sistema.consultarMultasPendientes(matricula);
      }
      
      
      
      
      
      
    
    
}
