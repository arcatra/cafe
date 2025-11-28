package cafe;

// Imports -----------------
import java.math.BigDecimal;
// -------------------------

public class MenuItem {
    int id;
    String itemName;
    boolean available;
    BigDecimal price;

    public MenuItem(int id, String itemName, boolean available, BigDecimal price) {
        this.id = id;
        this.itemName = itemName;
        this.available = available;
        this.price = price;
    }
}
