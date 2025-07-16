
public class Product {
    private final String name;
    private double price;

    // Constructor
    public Product(String name, double price) {
        this.name = name;
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = price;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    // Setter for price
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = price;
    }

    // Method to display product details
    public void displayInfo() {
        System.out.println("Product: " + name);
        System.out.println("Price: $" + price);
        System.out.println("--------------------------");
    }

    // Override equals and hashCode for proper comparison in Maps and Sets
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Product)) return false;
        Product p = (Product) obj;
        return this.name.equalsIgnoreCase(p.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}