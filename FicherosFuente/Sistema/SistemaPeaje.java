/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package practicapeajes.Sistema;

/**
 *
 * @author DavidArevaloRey
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import practicapeajes.Objetos.Multa;
import practicapeajes.Escaneres.RadarTramo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import practicapeajes.Datos.Datos;
import practicapeajes.Enumeraciones.TipoRadar;
import practicapeajes.Escaneres.CamaraPeaje;
import practicapeajes.Escaneres.RadarMovil;
import practicapeajes.Objetos.Ticket;
import practicapeajes.Objetos.Vehiculo;

//La clase sistemaPeaje es el nucleo principal de este sistema, aqui se unifican todas las clases creadas anteriormente que 
//posteriormente se relacionaran con los usuarios

public class SistemaPeaje implements Serializable {
    private List<Vehiculo> vehiculosCirculando = new ArrayList<>();
    private List<Multa> multas = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();
    private CamaraPeaje[] camarasPeaje;
    private int contadorVehiculosTotal;
    private static final String RUTA  = "FicherosDatos/";
    private static int NUMEROCARRILES = 4;
    
    public SistemaPeaje(){
        this.contadorVehiculosTotal = 0;
        this.camarasPeaje = new CamaraPeaje[NUMEROCARRILES];
        for(int i = 0; i < NUMEROCARRILES; i++){
            this.camarasPeaje[i] = new CamaraPeaje(i + 1);
        }
    
    }
    public void incrementarContadorVehiculo(){
        contadorVehiculosTotal++;
    }

    public void registrarEntrada(Vehiculo vehiculo, LocalDateTime horaEntrada){
        vehiculo.setEntrada(horaEntrada);
        vehiculosCirculando.add(vehiculo);
        Datos.guardarDatos(this);
    }

    public void registrarSalida(String matricula, LocalDateTime horaSalida, double tamano){
        Vehiculo encontrado = null;
        for(Vehiculo vehiculo : vehiculosCirculando){
            if(vehiculo.getMatricula().equals(matricula)){
                encontrado = vehiculo;
                
                break;
            }
        }

        if(encontrado != null){
            encontrado.setSalida(horaSalida);
            encontrado.setTamano(tamano);
            Ticket ticket = new Ticket(encontrado);
            tickets.add(ticket);

            Multa multa = RadarTramo.calcularMulta(encontrado);
            if(multa != null){
                multas.add(multa);
            }

            vehiculosCirculando.remove(encontrado);
                    Datos.guardarDatos(this);


        }
    }

    public void exportarHistorial(String matricula, LocalDateTime hora){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatterFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String nombreFicha = RUTA + matricula + "-" + hora.format(formatter) + ".txt";
        int contador = 0;
        File fichero = new File(RUTA);
        if(!fichero.exists()) {
            fichero.mkdirs();
        }

        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(nombreFicha)); 
            bw.write("Historial de peajes para matricula " + matricula + "\n");
            for(Ticket ticket : tickets){
                if(ticket.getMatricula().equalsIgnoreCase(matricula)){
                    contador++;
                    bw.write("nº " + contador + ": " + ticket.getFecha().format(formatterFecha) + "\n");

                }
            }
            
            bw.write("Total de veces que ha pasado: " + contador + "\n");
            
            if(contador == 0){
                System.out.println("No hay tickets para la matricula; " + matricula);
            
            }else{
                System.out.println("Historial exportado hacia: " + nombreFicha);
            }
        }catch (IOException e){
            System.err.println("Error durante la exportacion:" + e.getMessage());
        }

    }
    
    public List<Multa> buscarMultas(String matricula, TipoRadar Tipo, boolean pagado){
    List<Multa> encontrado = new ArrayList<>();
    for (Multa multa: multas){
        if(multa.getMatricula().equals(matricula) && multa.getTipo()== Tipo && multa.isPagada() == pagado){
        encontrado.add(multa);
        }
    }
    return encontrado;
    }

    public void hacerMultaMovil(String matricula, double velocidad, LocalDateTime fecha){
        Multa multa = RadarMovil.hacerMulta(matricula, fecha, velocidad);
        multas.add(multa);
        Datos.guardarDatos(this);

             
    }
    
    public boolean pagarMulta(Multa buscada){
        for(Multa multa : multas){
        if(multa.equals(buscada) && !multa.isPagada()){
            multa.pagar();
            Datos.guardarDatos(this);
            return true;
        }
        }
        return false;
    
    }
    
    public boolean pagarMulta(int i, String m){
        return pagarMulta(new Multa(m, i));
    }

    
    public List<Multa> getMultas(){
    return multas;
    }
    
    public void introducirDatosCamara(int carril, String matricula, double tamano){
        if(carril < 1 || carril > NUMEROCARRILES){
        System.out.println("Error en el numero de carril");
        return;
        }
        CamaraPeaje camaraActual = camarasPeaje[carril - 1]; //supongamos que los operarios pondran el carril 1,2,3,4 y no 0,1,2,3
        Vehiculo coche = new Vehiculo(matricula, tamano);
        coche.setEntrada(LocalDateTime.now());
        camaraActual.detectarVehiculo(coche);
        if(camaraActual.isTipo()){
        Vehiculo transmitido = camaraActual.transmitirDatos();
        if(transmitido != null){
            this.incrementarContadorVehiculo();
            this.vehiculosCirculando.add(transmitido);
            System.out.println("Vehiculo procesado");
            Datos.guardarDatos(this);
        
        
        }else{
            System.err.println("Error en la transmision de la camara");
        
        }
        
        }else{
        
            System.out.println("La camara del carril no esta transmitiendo nada");
        }
        
      
       
         }
    

    
    public List<Ticket> consultarTickets(String matricula){
        List<Ticket> r = new ArrayList<>();
        for(Ticket ticket : tickets){
            if(ticket.getMatricula().equalsIgnoreCase(matricula)){
            r.add(ticket);
            
            }
        }
        return r;
    }
    
    public List<Multa> consultarMultas(String matricula){
    
        List<Multa> recopilacion = new ArrayList<>();
        for (Multa multa : multas){
        if(multa.getMatricula().equalsIgnoreCase(matricula)){
            recopilacion.add(multa);
            }
        }
        return recopilacion;
   
    }
    public List<Multa> consultarMultasPorTipo(String matricula, TipoRadar tipo){
    
        List<Multa> recopilacion = new ArrayList<>();
        for (Multa multa : multas){
        if(multa.getMatricula().equalsIgnoreCase(matricula) && multa.getTipo() == tipo){
            
            recopilacion.add(multa);
            }
        }
        return recopilacion;
    }   
        public List<Multa> consultarMultasPendientes(String matricula){

        List<Multa> recopilacion = new ArrayList<>();
        for (Multa multa : multas){
        if(multa.getMatricula().equalsIgnoreCase(matricula) && !multa.isPagada()){
            
            recopilacion.add(multa);
            }
        }
        return recopilacion;
    }   
        
        
        public List<Multa> obtenerMultasPagadas(String matricula){
    
        List<Multa> recopilacion = new ArrayList<>();
        for (Multa multa : multas){
        if(multa.getMatricula().equalsIgnoreCase(matricula) && multa.isPagada()){
            
            recopilacion.add(multa);
            }
        }
        return recopilacion;
    }   
        
    public Vehiculo modificarTamanoVehiculo(String m, double n){
    for(Vehiculo v: vehiculosCirculando){
        if(v.getMatricula().equalsIgnoreCase(m)){
            if(n > 0){
            v.setTamano(n);
            Datos.guardarDatos(this);

            return v;
            }
            return null;
        }
    }
    return null;
    }

        
    public Ticket generarTicket(String matricula){
            
            for(Ticket t: tickets){
                if(t.getMatricula().equalsIgnoreCase(matricula)){
                return t;
                }
            }
            
    return null;
    }

  
}
