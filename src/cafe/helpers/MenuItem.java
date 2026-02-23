package cafe.helpers;

// Imports -----------------
import java.math.BigDecimal;
// -------------------------

public class MenuItem {
    public int id;
    public String itemName;
    public boolean available;
    public BigDecimal price;

    public MenuItem(int id, String itemName, boolean available, BigDecimal price) {
        this.id = id;
        this.itemName = itemName;
        this.available = available;
        this.price = price;
    }
}
