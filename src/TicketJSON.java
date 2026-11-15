import java.util.List;

public record TicketJSON(String cliente, String camarero, List<ProductoJSON> productos, double total) {
}
