/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package practicapeajes.Escaneres;

import java.io.Serializable;
import practicapeajes.Escaneres.Camara;
import practicapeajes.Objetos.Vehiculo;
import practicapeajes.Sistema.SistemaPeaje;

/**
 *
 * @author DavidArevaloRey
 */

/*
El objetivo de esta clase se implementar el uso de la camara de peaje, empleando 
como base la clase abstracta de la camara implementada anteriormente
La camara peaje debera registrar los datos del vehiculo
y transmitirlo
*/
public class CamaraPeaje extends Camara implements Serializable{
    private static final long serialVersionUID = 1L;
    private int carril; //segun los carriles que haya en la autopista
    private Vehiculo vehiculoVisto; //el vehiculo que capturara
    
    public CamaraPeaje(int id){
        super();
        this.carril = id;
    }
    
    public void detectarVehiculo(Vehiculo v) {
        this.vehiculoVisto = v;
        iniciarTransmision();
    }// si detecta el vehiculo activa la camara
    
    public int getCarril(){
    return carril;}

    public Vehiculo transmitirDatos(){
        Vehiculo variable = vehiculoVisto;
        vehiculoVisto = null;
        activar();
        
        return variable;

    }// si encuentra un vehiculo incrementa el numero de coches que hay en total y retorna el coche
}
