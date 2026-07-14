package practicapeajes.Pruebas;

import java.time.LocalDateTime;
import java.util.List;
import practicapeajes.Objetos.Multa;
import practicapeajes.Objetos.Ticket;
import practicapeajes.Objetos.Vehiculo;
import practicapeajes.Sistema.SistemaPeaje;

public class PruebasSistema {

    public static void main(String[] args) {
        pruebaSalidaSinEntradaNoGeneraTicket();
        pruebaRadarMovilSoloMultaConExceso();
        pruebaRegistroEntradaSalidaGeneraTicket();
        pruebaGenerarTicketDevuelveElUltimo();
        pruebaPagoMulta();
        System.out.println("Todas las pruebas del sistema han pasado.");
    }

    private static void pruebaSalidaSinEntradaNoGeneraTicket() {
        SistemaPeaje sistema = new SistemaPeaje();

        boolean salidaRegistrada = sistema.registrarSalida("0000AAA", LocalDateTime.now(), 12.0);

        comprobar(!salidaRegistrada, "No se debe registrar salida si no existe entrada previa");
        comprobar(sistema.consultarTickets("0000AAA").isEmpty(), "No debe crearse ticket para una salida inexistente");
    }

    private static void pruebaRadarMovilSoloMultaConExceso() {
        SistemaPeaje sistema = new SistemaPeaje();

        boolean multaSinExceso = sistema.hacerMultaMovil("1234ABC", 80.0, LocalDateTime.now());
        boolean multaEnLimite = sistema.hacerMultaMovil("1234ABC", 120.0, LocalDateTime.now());
        boolean multaConExceso = sistema.hacerMultaMovil("1234ABC", 121.0, LocalDateTime.now());

        comprobar(!multaSinExceso, "No se debe multar por debajo del limite");
        comprobar(!multaEnLimite, "No se debe multar exactamente en el limite");
        comprobar(multaConExceso, "Se debe multar cuando se supera el limite");
        comprobar(sistema.consultarMultas("1234ABC").size() == 1, "Solo debe existir una multa movil valida");
    }

    private static void pruebaRegistroEntradaSalidaGeneraTicket() {
        SistemaPeaje sistema = new SistemaPeaje();
        LocalDateTime entrada = LocalDateTime.of(2025, 5, 20, 9, 0);
        LocalDateTime salida = entrada.plusHours(1);

        boolean entradaRegistrada = sistema.registrarEntrada(new Vehiculo("1111BBB", 10.0), entrada);
        boolean salidaRegistrada = sistema.registrarSalida("1111BBB", salida, 10.0);

        List<Ticket> tickets = sistema.consultarTickets("1111BBB");
        comprobar(entradaRegistrada, "La entrada valida debe registrarse");
        comprobar(salidaRegistrada, "La salida valida debe registrarse");
        comprobar(tickets.size() == 1, "La salida debe generar un ticket");
        comprobar(tickets.get(0).getFecha().equals(salida), "El ticket debe conservar la fecha de salida");
    }

    private static void pruebaGenerarTicketDevuelveElUltimo() {
        SistemaPeaje sistema = new SistemaPeaje();
        LocalDateTime primeraEntrada = LocalDateTime.of(2025, 5, 20, 9, 0);
        LocalDateTime primeraSalida = primeraEntrada.plusHours(1);
        LocalDateTime segundaEntrada = LocalDateTime.of(2025, 5, 21, 9, 0);
        LocalDateTime segundaSalida = segundaEntrada.plusHours(1);

        sistema.registrarEntrada(new Vehiculo("2222CCC", 9.0), primeraEntrada);
        sistema.registrarSalida("2222CCC", primeraSalida, 9.0);
        sistema.registrarEntrada(new Vehiculo("2222CCC", 9.0), segundaEntrada);
        sistema.registrarSalida("2222CCC", segundaSalida, 9.0);

        Ticket ticket = sistema.generarTicket("2222CCC");

        comprobar(ticket != null, "Debe existir ticket para la matricula");
        comprobar(ticket.getFecha().equals(segundaSalida), "Debe devolverse el ticket mas reciente");
    }

    private static void pruebaPagoMulta() {
        SistemaPeaje sistema = new SistemaPeaje();

        sistema.hacerMultaMovil("3333DDD", 140.0, LocalDateTime.now());
        Multa multa = sistema.consultarMultas("3333DDD").get(0);

        comprobar(sistema.pagarMulta(multa.getId(), "3333DDD"), "Debe poder pagarse una multa pendiente");
        comprobar(!sistema.pagarMulta(multa.getId(), "3333DDD"), "No debe poder pagarse dos veces la misma multa");
        comprobar(sistema.obtenerMultasPagadas("3333DDD").size() == 1, "La multa pagada debe aparecer como abonada");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if(!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
