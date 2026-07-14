/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicapeajes.Datos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import practicapeajes.Sistema.SistemaPeaje;

/**
 *
 * @author DavidArevaloRey
 */


/*
El objetivo de esta clase es la persistencia de datos, queremos que en nuestro
programa del peaje, el sistema pueda guardar los datos que genere, y 
posteriormente cargar los datos en el mismo sistema
*/
public class Datos {
    private static final String RUTA  = "FicherosDatos/peajes.dat";
    //RUTA es la constante donde se ubicara nuestro fichero en binario
    public static void guardarDatos(SistemaPeaje sis) {//el sistema de peaje es el objeto que se va a guardar en el archivo binario 
        File archivo = new File(RUTA);
        File carpeta = archivo.getParentFile();
        if(carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA))) { //creamos un flujo de salida al archivo que queremos generar para escribir el objeto serializado
            oos.writeObject(sis); //serializa el objeto sis en archivo
            System.out.println("Datos guardados de forma correcta");
        }catch (IOException e){ //si surge un error de excepcion
            System.err.println(e.getMessage());
        }
    }
    /*
    Son metodos estaticos y publicos puesto que facilitara el uso de este metodo
    en las distintas partes del sistema como sea generar una multa nueva o introducir un 
    nuevo vehiculo que circule dentro del peaje
    */

    public static SistemaPeaje cargarDatos() { // se busca leer un archivo desde un archivo serializado
        File archivo = new File(RUTA);
        if(!archivo.exists()){
            File carpeta = archivo.getParentFile();
            if(carpeta != null && !carpeta.exists()) {
                carpeta.mkdirs();
            }
            return new SistemaPeaje();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RUTA))) {
            return (SistemaPeaje) ois.readObject(); //los objetos guardados los vuelve en el formato deseado y los pasa a nuestro sistema
        }catch (IOException | ClassNotFoundException e){ //error de excepcion
            System.err.println(e.getMessage());
            return new SistemaPeaje();
        }
    }
}
