import java.util.*;

/**
 * Clase principal del programa.
 *
 */
public class Main {

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        // El camarero será el mismo durante esta ejecución del programa.
        Camarero camarero = new Camarero("Carlos", "C01");

        Producto[] catalogo = {
                new Bebida("Café", 1.80, "Bebida", 10, "mediano"),
                new Comida("Bocadillo", 3.50, "Comida", 10, true),
                new Bebida("Zumo", 2.20, "Bebida", 10,"grande"),
                new Producto("Galleta", 1.00, "Dulce", 10),
                new Comida("Croissant", 1.70, "Comida", 10,false)
        };

        System.out.println(catalogo[1].mostrarInformacion());

        // Esta clase se encarga del archivo donde guardamos el historial.
        GestorTickets gestorTickets = new GestorTickets();

        System.out.println("=== NUEVO TICKET ===");
        Cliente cliente = crearCliente(teclado);
        Ticket ticketActual = new Ticket(cliente, camarero, 10);

        int opcion;

        do {
            mostrarMenu(ticketActual);
            opcion = leerEntero(teclado, "Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    mostrarCatalogo(catalogo);
                    break;

                case 2:
                    agregarProductoAlTicket(teclado, catalogo, ticketActual);
                    break;

                case 3:
                    ticketActual.mostrarTicket();
                    break;

                case 4:
                    aplicarDescuento(teclado, ticketActual);
                    break;

                case 5:
                    System.out.printf("Total actual del ticket: %.2f €%n",
                            ticketActual.calcularTotal());
                    break;

                case 6:
                    /*
                     * Finalizamos el ticket actual y lo guardamos en disco.
                     * Después se crea un nuevo cliente y un nuevo Ticket.
                     */
                    if (gestorTickets.guardarTicket(ticketActual)) {
                        System.out.println("Ticket nº "
                                + ticketActual.getNumeroTicket()
                                + " guardado correctamente.");

                        System.out.println("\n=== NUEVO TICKET ===");
                        cliente = crearCliente(teclado);
                        ticketActual = new Ticket(cliente, camarero, 10);

                        System.out.println("Nuevo ticket nº "
                                + ticketActual.getNumeroTicket()
                                + " creado.");
                    } else {
                        System.out.println("No se puede guardar un ticket vacío.");
                    }
                    break;

                case 7:
                    gestorTickets.mostrarTicketsGuardados();
                    break;

                case 8:
                    eliminarProductoTicket(teclado, ticketActual);
                    break;

                case 9:
                    mostrarProductosCategoria(catalogo);
                    break;

                case 10:
                    demostrarColaPedidos(camarero, catalogo);
                    break;

                case 11:
                    demostrarPrecioNegativo();
                    break;
                case 0:
                    if (ticketActual.getContadorProductos() > 0) {
                        System.out.println("ATENCIÓN: el ticket actual tiene productos "
                                + "pero no se ha finalizado ni guardado.");
                    }
                    System.out.println("Gracias por utilizar Cafetería Java.");
                    break;

                default:
                    System.out.println("Opción incorrecta. Inténtelo de nuevo.");
            }

            System.out.println();

        } while (opcion != 0);

        teclado.close();
    }

    /**
     * Muestra las opciones principales e indica qué ticket está abierto.
     */
    private static void mostrarMenu(Ticket ticketActual) {
        System.out.println("================================");
        System.out.println("         CAFETERÍA JAVA");
        System.out.println("       Ticket actual: nº "
                + ticketActual.getNumeroTicket());
        System.out.println("================================");
        System.out.println("1. Ver productos disponibles");
        System.out.println("2. Añadir producto al ticket actual");
        System.out.println("3. Mostrar ticket actual");
        System.out.println("4. Aplicar descuento a un producto");
        System.out.println("5. Consultar total actual");
        System.out.println("6. Finalizar y guardar ticket");
        System.out.println("7. Ver tickets guardados");
        System.out.println("8. Eliminar producto del ticket");
        System.out.println("9. Ver productos por categoria");
        System.out.println("10. Demostrar cola de pedidos");
        System.out.println("11. Demostrar precio negativo");
        System.out.println("0. Salir");
        System.out.println("================================");
    }

    /**
     * Crea un cliente usando los datos introducidos por teclado.
     */
    private static Cliente crearCliente(Scanner teclado) {
        String nombre;
        String telefono;

        do {
            System.out.print("Nombre del cliente: ");
            nombre = teclado.nextLine().trim();

            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
            }
        } while (nombre.isEmpty());

        System.out.print("Teléfono del cliente: ");
        telefono = teclado.nextLine().trim();

        return new Cliente(nombre, telefono);
    }

    /**
     * Recorre el array del catálogo y muestra todos los productos.
     */
    private static void mostrarCatalogo(Producto[] catalogo) {
        System.out.println("\n--- PRODUCTOS DISPONIBLES ---");

        for (int i = 0; i < catalogo.length; i++) {
            System.out.println((i + 1) + ". " + catalogo[i].mostrarInformacion());
        }
    }

    /**
     * Permite elegir un producto del catálogo y añadirlo al ticket actual.
     */
    private static void agregarProductoAlTicket(Scanner teclado,
                                                 Producto[] catalogo,
                                                 Ticket ticket) {
        mostrarCatalogo(catalogo);

        int numeroProducto = leerEntero(teclado,
                "Seleccione el número del producto que desea añadir: ");

        if (numeroProducto < 1 || numeroProducto > catalogo.length) {
            System.out.println("El producto seleccionado no existe.");
            return;
        }

        Producto productoElegido = catalogo[numeroProducto - 1];

        int cantidad = leerEntero(teclado, "Introduce cantidad: ");

        boolean agregado = ticket.agregarProducto(productoElegido, cantidad);

        if (agregado) {
            System.out.println("Producto añadido correctamente: "
                    + productoElegido.getNombre());
        }
    }

    /**
     * Aplica un descuento a un producto para demostrar la interfaz
     * Descontable. Igual que en la versión anterior, el descuento se calcula
     * y se muestra, pero no modifica el precio original del catálogo.
     */
    private static void aplicarDescuento(Scanner teclado, Ticket ticket) {

        if (ticket.getContadorProductos() == 0) {
            System.out.println("No hay productos en el ticket.");
            return;
        }

        System.out.println("\n--- PRODUCTOS DEL TICKET ---");

        ArrayList<Producto> productos = ticket.getProductos();

        for (int i = 0; i < ticket.getContadorProductos(); i++) {
            System.out.println((i + 1) + ". " + productos.get(i).mostrarInformacion());
        }

        int numeroProducto = leerEntero(teclado,
                "Seleccione el producto al que desea aplicar el descuento: ");

        if (numeroProducto < 1 || numeroProducto > ticket.getContadorProductos()) {
            System.out.println("El producto seleccionado no existe en el ticket.");
            return;
        }

        double porcentaje = leerDouble(teclado,
                "Introduzca el porcentaje de descuento (0-100): ");

        if (porcentaje < 0 || porcentaje > 100) {
            System.out.println("El porcentaje debe estar entre 0 y 100.");
            return;
        }

        Producto producto = productos.get(numeroProducto -1);
        Descontable descontable = producto;
        double precioConDescuento = descontable.aplicarDescuento(porcentaje);

        System.out.println("\n--- DESCUENTO ---");
        System.out.println("Producto: " + producto.getNombre());
        System.out.printf("Precio original: %.2f €%n", producto.getPrecio());
        System.out.printf("Descuento aplicado: %.2f%%%n", porcentaje);
        System.out.printf("Precio con descuento: %.2f €%n", precioConDescuento);
    }

    private static void eliminarProductoTicket(Scanner teclado, Ticket ticketActual){
        if (ticketActual.getContadorProductos() == 0) {
            System.out.println("No hay productos en el ticket.");
            return;
        }

        ticketActual.mostrarTicket();
        int posicion = leerEntero(teclado,
                "Introduce el número de producto que quieres eliminar: ");

        if (ticketActual.eliminarProducto(posicion)) {
            System.out.println("Producto eliminado del ticket");
        } else {
            System.out.println("El producto seleccionado no existe en el ticket");
        }

    }

    private static void mostrarProductosCategoria(Producto[] catalogo){
        HashMap<String, ArrayList<Producto>> categorias = new HashMap<>();

        for (Producto producto : catalogo) {
            String categoria = producto.getCategoria();

            if (!categorias.containsKey(categoria)) {
                categorias.put(categoria, new ArrayList<>());
            }

            categorias.get(categoria).add(producto);

        }

        for (Map.Entry<String, ArrayList<Producto>> entrada : categorias.entrySet()) {
            System.out.println(entrada.getKey());
            for (Producto producto : entrada.getValue()) {
                System.out.println(producto.mostrarInformacion());
            }

            }
    }

    private static void demostrarColaPedidos(Camarero camarero, Producto[] catalogo){
        Queue<Ticket> pedidos = new LinkedList<>();

        Cliente cliente1 = new Cliente("Dani", "600885323");
        Ticket ticket1 = new Ticket(cliente1, camarero, 10);
        ticket1.agregarProducto(catalogo[0],1);
        pedidos.offer(ticket1);

        Cliente cliente2 = new Cliente("Carlos", "695685323");
        Ticket ticket2 = new Ticket(cliente2, camarero, 10);
        ticket2.agregarProducto(catalogo[1],1);
        pedidos.offer(ticket2);

        Cliente cliente3 = new Cliente("Alberto", "611885777");
        Ticket ticket3 = new Ticket(cliente3, camarero, 10);
        ticket3.agregarProducto(catalogo[2],1);
        pedidos.offer(ticket3);

        Ticket atendido = pedidos.poll();
        System.out.println("Atendiendo ticket: ");
        atendido.mostrarTicket();
        System.out.println("Tickets pendientes: " + pedidos.size());

        pedidos.poll();
        pedidos.poll();
        System.out.println("Tickets pendientes: " + pedidos.size());

        Ticket siguiente = pedidos.poll();
        if (siguiente == null){
            System.out.println("No hay tickets pendientes");
        }else {
            siguiente.mostrarTicket();
        }

    }

    private static void demostrarPrecioNegativo(){
        try{
            Producto producto = new Producto("Producto", -2.5, "Dulce", 10);
            System.out.println("Producto creado: " + producto.getNombre());
        }catch (IllegalArgumentException e){
            System.out.println("Error al crear el producto: " + e.getMessage());
        }
    }


    private static int leerEntero(Scanner teclado, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = teclado.nextLine();

            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Debe introducir un número entero.");
            }
        }
    }

    private static double leerDouble(Scanner teclado, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = teclado.nextLine().replace(',', '.');

            try {
                return Double.parseDouble(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Debe introducir un número válido.");
            }
        }
    }
}
