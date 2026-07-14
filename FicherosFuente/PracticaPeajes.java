/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package practicapeajes;

import practicapeajes.Datos.Datos;
import practicapeajes.IU.IU_Principal;
import practicapeajes.Sistema.SistemaPeaje;

/**
 *
 * @author DavidArevaloRey
 */
public class PracticaPeajes {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                new IU_Principal().setVisible(true);
            }
    
        });
        
    }
    
}
