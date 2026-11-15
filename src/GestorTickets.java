import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;

/**
 * Se encarga de guardar y consultar los tickets finalizados.
 *
 * Los tickets se almacenan en un archivo de texto llamado "tickets.txt".
 * De esta forma el historial permanece disponible aunque cerremos el programa
 * y volvamos a ejecutarlo más tarde.
 */
public class GestorTickets {

    private static final String NOMBRE_ARCHIVO = "tickets.txt";

    /**
     * Guarda un ticket al final del archivo.
     *
     * FileWriter con el parámetro true trabaja en modo "añadir", por lo que
     * no borra los tickets guardados anteriormente.
     *
     * @return true si se ha guardado correctamente
     */
    public boolean guardarTicket(Ticket ticket) {
        if (ticket == null || ticket.getContadorProductos() == 0) {
            return false;
        }

        try (BufferedWriter escritor = new BufferedWriter(
                new FileWriter(NOMBRE_ARCHIVO, true))) {

            escritor.write(ticket.generarTextoTicket());
            escritor.write("\n");
            return true;

        } catch (IOException e) {
            System.out.println("Error al guardar el ticket: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lee el archivo línea a línea y muestra todos los tickets almacenados.
     */
    public void mostrarTicketsGuardados() {
        File archivo = new File(NOMBRE_ARCHIVO);

        if (!archivo.exists() || archivo.length() == 0) {
            System.out.println("Todavía no hay tickets guardados.");
            return;
        }

        System.out.println("\n========== HISTORIAL DE TICKETS ==========");

        try (BufferedReader lector = new BufferedReader(
                new FileReader(archivo))) {

            String linea;
            while ((linea = lector.readLine()) != null) {
                System.out.println(linea);
            }

        } catch (IOException e) {
            System.out.println("Error al leer los tickets: " + e.getMessage());
        }
    }

    public void exportarJSON(Ticket ticket) {
        // 1. Convertir los productos del ticket a ProductoJSON
        ArrayList<ProductoJSON> productoJSONS = new ArrayList<>();
        for ( Producto producto : ticket.getProductos()){
            productoJSONS.add(new ProductoJSON(producto.getNombre(), producto.getPrecio()));
        }
        // 2. Crear el TicketJSON
        double total = Math.round(ticket.calcularTotal()*100) / 100.0;

        TicketJSON ticketJSON = new TicketJSON(ticket.getCliente().getNombre(), ticket.getCamarero().getNombre(), productoJSONS, total);
        // 3. Escribirlo en ticket.json con Jackson (try-catch)

        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("ticket.json"), ticketJSON);
            System.out.println("Ticket exportado a JSON");
        }catch (IOException e){
            System.out.println("Error al exportar el ticket: " + e.getMessage());
        }
    }
}
