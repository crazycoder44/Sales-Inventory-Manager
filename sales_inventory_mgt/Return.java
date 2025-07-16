import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Return {
    public enum ReturnAction {
        REFUND, REPLACE
    }

    private final String purchaseID;
    private final Product product;
    private final int quantityReturned;
    private final ReturnAction action;
    private final double refundAmount; // Only applies to REFUND
    private final String reason;
    private final String returnTime;


    // Constructor
    public Return(String purchaseID, Product product, int quantityReturned, ReturnAction action, double unitPrice, String reason) {
        this.purchaseID = purchaseID;
        this.product = product;
        this.quantityReturned = quantityReturned;
        this.action = action;
        this.reason = reason;
        this.refundAmount = (action == ReturnAction.REFUND) ? unitPrice * quantityReturned : 0;
        this.returnTime = generateReturnTime();
    }

    // Timestamp generator
    private String generateReturnTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(fmt);
    }

    // Getters
    public String getPurchaseID() { return purchaseID; }
    public Product getProduct() { return product; }
    public int getQuantityReturned() { return quantityReturned; }
    public ReturnAction getAction() { return action; }
    public double getRefundAmount() { return refundAmount; }
    public String getReason() { return reason; }
    public String getReturnTime() { return returnTime; }


    // Display return details
    public void displayReturnDetails() {
        System.out.println("---- Return Record ----");
        System.out.println("Purchase ID: " + purchaseID);
        System.out.println("Product: " + product.getName());
        System.out.println("Returned Quantity: " + quantityReturned);
        System.out.println("Action: " + action);
        if (action == ReturnAction.REFUND) {
            System.out.println("Refund Amount: $" + refundAmount);
        } else {
            System.out.println("Item replaced. Marked unsellable in inventory.");
        }
        System.out.println("Reason: " + reason);
        System.out.println("Return Time: " + returnTime);
        System.out.println("------------------------\n");
    }
}