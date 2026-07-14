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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import practicapeajes.Datos.Datos;
import practicapeajes.Enumeraciones.TipoRadar;
import practicapeajes.Escaneres.CamaraPeaje;
import practicapeajes.Escaneres.RadarMovil;
import practicapeajes.Objetos.Ticket;
import practicapeajes.Objetos.Vehiculo;

//La clase sistemaPeaje es el nucleo principal de este sistema, aqui se unifican todas las clases creadas anteriormente que 
//posteriormente se relacionaran con los usuarios

public class SistemaPeaje implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<Vehiculo> vehiculosCirculando = new ArrayList<>();
    private ArrayList<Multa> multas = new ArrayList<>();
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private CamaraPeaje[] camarasPeaje;
    private int contadorVehiculosTotal;
    private static final String RUTA  = "FicherosDatos/";
    private static final int NUMEROCARRILES = 4;
    
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

    public boolean registrarEntrada(Vehiculo vehiculo, LocalDateTime horaEntrada){
        if(vehiculo == null || horaEntrada == null || !matriculaValida(vehiculo.getMatricula())){
            return false;
        }
        for(Vehiculo actual : vehiculosCirculando){
            if(actual.getMatricula().equalsIgnoreCase(vehiculo.getMatricula().trim())){
                return false;
            }
        }
        vehiculo.setEntrada(horaEntrada);
        vehiculosCirculando.add(vehiculo);
        incrementarContadorVehiculo();
        Datos.guardarDatos(this);
        return true;
    }

    public boolean registrarSalida(String matricula, LocalDateTime horaSalida, double tamano){
        if(!matriculaValida(matricula) || horaSalida == null || tamano <= 0){
            return false;
        }
        Vehiculo encontrado = null;
        for(Vehiculo vehiculo : vehiculosCirculando){
            if(vehiculo.getMatricula().equalsIgnoreCase(matricula.trim())){
                encontrado = vehiculo;
                
                break;
            }
        }

        if(encontrado != null){
            if(encontrado.getEntrada() != null && !horaSalida.isAfter(encontrado.getEntrada())){
                return false;
            }
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
            return true;
        }
        return false;
    }

    public boolean exportarHistorial(String matricula, LocalDateTime hora){
        if(!matriculaValida(matricula) || hora == null){
            return false;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatterFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String nombreFicha = RUTA + matricula.trim() + "-" + hora.format(formatter) + ".txt";
        int contador = 0;
        File fichero = new File(RUTA);
        if(!fichero.exists()) {
            fichero.mkdirs();
        }

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(nombreFicha))){
            bw.write("Historial de peajes para matricula " + matricula.trim() + "\n");
            for(Ticket ticket : tickets){
                if(ticket.getMatricula().equalsIgnoreCase(matricula.trim())){
                    contador++;
                    bw.write("num. " + contador + ": " + ticket.getFecha().format(formatterFecha) + "\n");

                }
            }
            
            bw.write("Total de veces que ha pasado: " + contador + "\n");
            
            if(contador == 0){
                System.out.println("No hay tickets para la matricula; " + matricula);
            
            }else{
                System.out.println("Historial exportado hacia: " + nombreFicha);
            }
            return contador > 0;
        }catch (IOException e){
            System.err.println("Error durante la exportacion:" + e.getMessage());
            return false;
        }

    }
    
    public List<Multa> buscarMultas(String matricula, TipoRadar Tipo, boolean pagado){
    List<Multa> encontrado = new ArrayList<>();
    if(!matriculaValida(matricula) || Tipo == null){
        return encontrado;
    }
    for (Multa multa: multas){
        if(multa.getMatricula().equalsIgnoreCase(matricula.trim()) && multa.getTipo()== Tipo && multa.isPagada() == pagado){
        encontrado.add(multa);
        }
    }
    return encontrado;
    }

    public boolean hacerMultaMovil(String matricula, double velocidad, LocalDateTime fecha){
        if(!matriculaValida(matricula) || fecha == null || velocidad <= 0){
            return false;
        }
        Multa multa = RadarMovil.hacerMulta(matricula, fecha, velocidad);
        if(multa == null){
            return false;
        }
        multas.add(multa);
        Datos.guardarDatos(this);
        return true;

             
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
    return new ArrayList<>(multas);
    }

    public boolean registrarMulta(Multa multa){
        if(multa == null){
            return false;
        }
        multas.add(multa);
        Datos.guardarDatos(this);
        return true;
    }
    
    public boolean introducirDatosCamara(int carril, String matricula, double tamano){
        if(carril < 1 || carril > NUMEROCARRILES){
        System.out.println("Error en el numero de carril");
        return false;
        }
        if(!matriculaValida(matricula) || tamano <= 0){
            System.out.println("Datos del vehiculo no validos");
            return false;
        }
        CamaraPeaje camaraActual = camarasPeaje[carril - 1]; //supongamos que los operarios pondran el carril 1,2,3,4 y no 0,1,2,3
        Vehiculo coche = new Vehiculo(matricula.trim(), tamano);
        coche.setEntrada(LocalDateTime.now());
        camaraActual.detectarVehiculo(coche);
        if(camaraActual.isTipo()){
        Vehiculo transmitido = camaraActual.transmitirDatos();
        if(transmitido != null){
            this.incrementarContadorVehiculo();
            this.vehiculosCirculando.add(transmitido);
            System.out.println("Vehiculo procesado");
            Datos.guardarDatos(this);
            return true;
        
        
        }else{
            System.err.println("Error en la transmision de la camara");
            return false;
        
        }
        
        }else{
        
            System.out.println("La camara del carril no esta transmitiendo nada");
            return false;
        }
    }
    

    
    public List<Ticket> consultarTickets(String matricula){
        List<Ticket> r = new ArrayList<>();
        if(!matriculaValida(matricula)){
            return r;
        }
        for(Ticket ticket : tickets){
            if(ticket.getMatricula().equalsIgnoreCase(matricula.trim())){
            r.add(ticket);
            
            }
        }
        return r;
    }
    
    public List<Multa> consultarMultas(String matricula){
    
        List<Multa> recopilacion = new ArrayList<>();
        if(!matriculaValida(matricula)){
            return recopilacion;
        }
        for (Multa multa : multas){
        if(multa.getMatricula().equalsIgnoreCase(matricula.trim())){
            recopilacion.add(multa);
            }
        }
        return recopilacion;
   
    }
    public List<Multa> consultarMultasPorTipo(String matricula, TipoRadar tipo){
    
        List<Multa> recopilacion = new ArrayList<>();
        if(!matriculaValida(matricula) || tipo == null){
            return recopilacion;
        }
        for (Multa multa : multas){
        if(multa.getMatricula().equalsIgnoreCase(matricula.trim()) && multa.getTipo() == tipo){
            
            recopilacion.add(multa);
            }
        }
        return recopilacion;
    }   
        public List<Multa> consultarMultasPendientes(String matricula){

        List<Multa> recopilacion = new ArrayList<>();
        if(!matriculaValida(matricula)){
            return recopilacion;
        }
        for (Multa multa : multas){
        if(multa.getMatricula().equalsIgnoreCase(matricula.trim()) && !multa.isPagada()){
            
            recopilacion.add(multa);
            }
        }
        return recopilacion;
    }   
        
        
        public List<Multa> obtenerMultasPagadas(String matricula){
    
        List<Multa> recopilacion = new ArrayList<>();
        if(!matriculaValida(matricula)){
            return recopilacion;
        }
        for (Multa multa : multas){
        if(multa.getMatricula().equalsIgnoreCase(matricula.trim()) && multa.isPagada()){
            
            recopilacion.add(multa);
            }
        }
        return recopilacion;
    }   
        
    public Vehiculo modificarTamanoVehiculo(String m, double n){
    if(!matriculaValida(m) || n <= 0){
        return null;
    }
    for(Vehiculo v: vehiculosCirculando){
        if(v.getMatricula().equalsIgnoreCase(m.trim())){
            v.setTamano(n);
            Datos.guardarDatos(this);

            return v;
        }
    }
    return null;
    }

        
    public Ticket generarTicket(String matricula){
            if(!matriculaValida(matricula)){
                return null;
            }
            
            for(int i = tickets.size() - 1; i >= 0; i--){
                Ticket t = tickets.get(i);
                if(t.getMatricula().equalsIgnoreCase(matricula.trim())){
                return t;
                }
            }
            
    return null;
    }

    private boolean matriculaValida(String matricula){
        return matricula != null && !matricula.trim().isEmpty();
    }

  
}
