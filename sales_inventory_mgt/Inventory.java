
import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private final Map<Product, Integer> stock;

    // Constructor
    public Inventory() {
        stock = new HashMap<>();
    }

    // Add product with quantity (new or restock)
    public void addStock(Product product, int quantity) {
        stock.put(product, stock.getOrDefault(product, 0) + quantity);
        System.out.println(quantity + " unit(s) of " + product.getName() + " added to inventory.");
    }

    // Deduct stock for a sale
    public boolean deductStock(Product product, int quantity) {
        int currentQty = stock.getOrDefault(product, 0);
        if (quantity > currentQty) {
            System.out.println("Not enough stock for " + product.getName());
            return false;
        }
        stock.put(product, currentQty - quantity);
        System.out.println(quantity + " unit(s) of " + product.getName() + " sold.");
        return true;
    }

    // Get current stock level
    public int getStockLevel(Product product) {
        return stock.getOrDefault(product, 0);
    }

    // Check if product exists in inventory
    public boolean isInInventory(Product product) {
        return stock.containsKey(product);
    }

    // Remove product from inventory
    public boolean removeProduct(Product product) {
        if (stock.containsKey(product)) {
            if (stock.get(product) > 0) {
                System.out.println("Cannot remove " + product.getName() + ": still in stock.");
                return false;
            }
            stock.remove(product);
            System.out.println(product.getName() + " removed from inventory.");
            return true;
        }
        System.out.println("Product not found in inventory.");
        return false;
    }

    // Display all stock levels
    public void displayInventory() {
        System.out.println("\n--- Inventory ---");
        for (Map.Entry<Product, Integer> entry : stock.entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            System.out.println("Product: " + p.getName() + " | Price: $" + p.getPrice() + " | Quantity: " + qty);
        }
        System.out.println("----------------------\n");
    }
}