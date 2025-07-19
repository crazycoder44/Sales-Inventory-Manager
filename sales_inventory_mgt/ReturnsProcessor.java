import java.util.List;
import java.util.Map;
import java.util.HashMap;

public final class ReturnsProcessor {

    private ReturnsProcessor() {
        // Utility class
    }

    public static Return processReturn(
        String purchaseID,
        Product product,
        int quantityReturned,
        Return.ReturnAction action,
        String reason,
        double unitPrice,
        Inventory inventory,
        List<Sale> salesLog,
        List<Return> returnLog
    ) {
        if (quantityReturned <= 0) {
            System.out.println("Invalid return quantity.");
            return null;
        }

        // Step 1: Total sold
        int totalSold = 0;
        for (Sale s : salesLog) {
            if (s.getPurchaseID().equals(purchaseID) && s.getProduct().equals(product)) {
                totalSold += s.getQuantitySold();
            }
        }

        if (totalSold == 0) {
            System.out.println("No record of product '" + product.getName() + "' under this purchase ID.");
            return null;
        }

        // Step 2: Total refunded
        int totalRefunded = 0;
        for (Return r : returnLog) {
            if (r.getPurchaseID().equals(purchaseID) &&
                r.getProduct().equals(product) &&
                r.getAction() == Return.ReturnAction.REFUND) {
                totalRefunded += r.getQuantityReturned();
            }
        }

        int remainingRefundableQty = totalSold - totalRefunded;

        if (action == Return.ReturnAction.REFUND && quantityReturned > remainingRefundableQty) {
            System.out.println("Cannot refund more than " + remainingRefundableQty + " unit(s).");
            return null;
        }

        Return returnObj = new Return(purchaseID, product, quantityReturned, action, unitPrice, reason);

        if (action == Return.ReturnAction.REFUND) {
            System.out.println("Return accepted. $" + returnObj.getRefundAmount() + " refunded to buyer.");
        } else {
            boolean removed = inventory.deductStock(product, quantityReturned);
            if (removed) {
                System.out.println("Items replaced and marked unsellable.");
            } else {
                System.out.println("Replacement failed due to insufficient stock.");
            }
        }

        returnLog.add(returnObj);
        returnObj.displayReturnDetails();
        return returnObj;
    }

    public static void getReturnableSummaryByPurchaseID(
        String purchaseID,
        List<Sale> salesLog,
        List<Return> returnLog
    ) {
        System.out.println("\nReturnable Summary for Purchase ID: " + purchaseID);
        Map<Product, Integer> totalSoldMap = new HashMap<>();
        Map<Product, Integer> refundedMap = new HashMap<>();

        for (Sale sale : salesLog) {
            if (sale.getPurchaseID().equals(purchaseID)) {
                Product product = sale.getProduct();
                totalSoldMap.put(product, totalSoldMap.getOrDefault(product, 0) + sale.getQuantitySold());
            }
        }

        for (Return ret : returnLog) {
            if (ret.getPurchaseID().equals(purchaseID) &&
                ret.getAction() == Return.ReturnAction.REFUND) {
                Product product = ret.getProduct();
                refundedMap.put(product, refundedMap.getOrDefault(product, 0) + ret.getQuantityReturned());
            }
        }

        if (totalSoldMap.isEmpty()) {
            System.out.println("No products found for this purchase ID.");
            return;
        }

        for (Product product : totalSoldMap.keySet()) {
            int soldQty = totalSoldMap.get(product);
            int refundedQty = refundedMap.getOrDefault(product, 0);
            int remainingQty = soldQty - refundedQty;

            System.out.println("- " + product.getName() +
                ": Sold = " + soldQty +
                ", Refunded = " + refundedQty +
                ", Eligible for REFUND = " + remainingQty);
        }

        System.out.println("-----------------------------\n");
    }

    // Returnable quantity calculator for specific product
    public static int getRemainingRefundableQuantity(
        String purchaseID,
        Product product,
        List<Sale> salesLog,
        List<Return> returnLog
    ) {
        int soldQty = 0;
        for (Sale s : salesLog) {
            if (s.getPurchaseID().equals(purchaseID) && s.getProduct().equals(product)) {
                soldQty += s.getQuantitySold();
            }
        }

        int refundedQty = 0;
        for (Return r : returnLog) {
            if (r.getPurchaseID().equals(purchaseID) && r.getProduct().equals(product)
                && r.getAction() == Return.ReturnAction.REFUND) {
                refundedQty += r.getQuantityReturned();
            }
        }

        return soldQty - refundedQty;
    }
}