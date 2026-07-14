/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package practicapeajes.Escaneres;

import java.io.Serializable;

/**
 *
 * @author DavidArevaloRey
 */

/*
Crearemos camara para que sea una clase abstracta que permitira transpasar a sus hijos
su constructor, sus variables y sus funciones.
En conclusion servira como base

*/
public abstract class Camara implements Serializable {
     private static final long serialVersionUID = 1L;
     private boolean enTransmision;
    public Camara() {
        this.enTransmision = false;
    }//inicializamos que al pricipio la camara este desconectada

    public void activar(){
        this.enTransmision = false;
    }// la desactivamos

    public boolean isTipo() {
        return enTransmision;
    } // vemos el tipo 
    
    public void iniciarTransmision(){
        this.enTransmision = true;
    } //iniciamos la transmision
    
    
}
