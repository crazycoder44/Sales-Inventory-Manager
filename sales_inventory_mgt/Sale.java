import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sale {
    private final Product product;
    private final int quantitySold;
    private final double saleAmount;
    private final String timestamp;
    private final String purchaseID;

    // Constructor
    public Sale(Product product, int quantitySold, double saleAmount, String purchaseID) {
        this.product = product;
        this.quantitySold = quantitySold;
        this.saleAmount = saleAmount;
        this.purchaseID = purchaseID;
        this.timestamp = generateTimestamp(); // Generate timestamp for the sale
    }

    // Generate formatted time of sale
    private String generateTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    // Getters
    public Product getProduct() {
        return product;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public double getSaleAmount() {
        return saleAmount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPurchaseID() {
        return purchaseID;
    }


    // Display sale details
    public void displaySaleDetails() {
        System.out.println("---- Sale Record ----");
        System.out.println("Purchase ID: " + purchaseID);
        System.out.println("Product: " + product.getName());
        System.out.println("Price: $" + product.getPrice());
        System.out.println("Quantity Sold: " + quantitySold);
        System.out.println("Total Sale Amount: $" + saleAmount);
        System.out.println("Time of Sale: " + timestamp);
        System.out.println("---------------------\n");
    }

    // Static method to display all sales under a given purchase ID
    public static void displayPurchaseDetails(String purchaseID, java.util.List<Sale> salesLog) {
        System.out.println("\n🧾 PURCHASE RECEIPT - ID: " + purchaseID);
        double total = 0;
        for (Sale s : salesLog) {
            if (s.getPurchaseID().equals(purchaseID)) {
                System.out.println(s.product.getName() + " - $" + s.product.getPrice() +
                                   " x " + s.quantitySold + " = $" + s.saleAmount);
                total += s.saleAmount;
            }
        }
        System.out.println("Total Paid: $" + total);
        System.out.println("🎉 Thank you for supporting CrazyMart!\n");
    }

}