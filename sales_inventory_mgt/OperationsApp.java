import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OperationsApp {

    public static void runUserMenu(
        Scanner scanner,
        Inventory inventory,
        List<Product> productCatalog,
        List<Sale> salesLog,
        List<Return> returnLog
    ) {
        while (true) {
            System.out.println("\n=== USER OPERATIONS ===");
            System.out.println("1. Make a Sale");
            System.out.println("2. Process a Return");
            System.out.println("3. Exit to Main Menu");
            System.out.print("Select option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    boolean hasStock = productCatalog.stream()
                        .anyMatch(p -> inventory.isInInventory(p) && inventory.getStockLevel(p) > 0);

                    if (!hasStock) {
                        System.out.println("\nThere are no goods in stock at this time. Please check back later.");
                        AdminApp.sleepBriefly("Returning to User Menu...");
                        break;
                    }

                    List<SalesProcessor.ProductSelection> basket =
                        SalesProcessor.buildSaleBasket(scanner, productCatalog, inventory);
                    
                    String role = "";
                    boolean validRole = false;

                    while (!validRole) {
                        System.out.println("\nSelect buyer role:");
                        System.out.println("1. Junior Developer");
                        System.out.println("2. Senior Developer");
                        System.out.println("3. CTO");
                        System.out.print("Enter option (1, 2, or 3): ");

                        String roleOption = scanner.nextLine().trim();

                        switch (roleOption) {
                            case "1" -> {
                                role = "Junior Developer";
                                validRole = true;
                            }
                            case "2" -> {
                                role = "Senior Developer";
                                validRole = true;
                            }
                            case "3" -> {
                                role = "CTO";
                                validRole = true;
                            }
                            default -> {
                                System.out.println("Invalid selection. Please enter 1, 2 or 3.");
                            }
                        }
                    }
                    
                    SalesProcessor.finalizePurchase(basket, role, inventory, salesLog);
                }
                case "2" -> {
                    runReturnFlow(scanner, inventory, salesLog, returnLog);
                }
                case "3" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> AdminApp.sleepBriefly("\nInvalid option. Please try again.");
            }
        }
    }

    // Fully integrated return processing flow
    private static void runReturnFlow(
        Scanner scanner,
        Inventory inventory,
        List<Sale> salesLog,
        List<Return> returnLog
    ) {
        System.out.print("Enter Purchase ID to process return: ");
        String purchaseID = scanner.nextLine().trim();

        boolean purchaseExists = false;
        for (Sale sale : salesLog) {
            if (sale.getPurchaseID().equals(purchaseID)) {
                purchaseExists = true;
                break;
            }
        }

        if (!purchaseExists) {
            System.out.println("No purchase found with that ID.");
            AdminApp.sleepBriefly("Returning to User Menu...");
            return;
        }

        ReturnsProcessor.getReturnableSummaryByPurchaseID(purchaseID, salesLog, returnLog);

        List<Product> productOptions = new ArrayList<>();
        for (Sale sale : salesLog) {
            if (sale.getPurchaseID().equals(purchaseID)) {
                if (!productOptions.contains(sale.getProduct())) {
                    productOptions.add(sale.getProduct());
                }
            }
        }

        Product selectedProduct = null;
        while (selectedProduct == null) {
            System.out.println("\nSelect product to return:");
            for (int i = 0; i < productOptions.size(); i++) {
                System.out.println((i + 1) + ". " + productOptions.get(i).getName());
            }

            System.out.print("Enter option number: ");
            int productChoice;
            try {
                productChoice = Integer.parseInt(scanner.nextLine()) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid selection. Please try again.");
                continue;
            }

            if (productChoice < 0 || productChoice >= productOptions.size()) {
                System.out.println("Invalid selection. Please try again.");
            } else {
                Product candidate = productOptions.get(productChoice);
                int eligibleQty = ReturnsProcessor.getRemainingRefundableQuantity(
                    purchaseID, candidate, salesLog, returnLog);

                if (eligibleQty <= 0) {
                    System.out.println("No refundable quantity left for that product.");
                } else {
                    selectedProduct = candidate;
                }
            }
        }

        int eligibleQty = ReturnsProcessor.getRemainingRefundableQuantity(
            purchaseID, selectedProduct, salesLog, returnLog);

        int quantity = -1;
        while (true) {
            System.out.print("Enter quantity to return (max " + eligibleQty + "): ");
            String input = scanner.nextLine();

            try {
                quantity = Integer.parseInt(input);

                if (quantity <= 0 || quantity > eligibleQty) {
                    System.out.println("Invalid quantity. Please enter a valid quantity.");
                } else {
                    break;
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity. Please enter a valid quantity.");
            }
        }

        Return.ReturnAction action = null;
        while (action == null) {
            System.out.println("\nChoose return action:");
            System.out.println("1. REFUND");
            System.out.println("2. REPLACE");
            System.out.print("Enter option (1 or 2): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> action = Return.ReturnAction.REFUND;
                case "2" -> action = Return.ReturnAction.REPLACE;
                default -> System.out.println("Invalid selection. Please enter 1 or 2.");
            }
        }

        if (action == Return.ReturnAction.REPLACE &&
            inventory.getStockLevel(selectedProduct) < quantity) {

            System.out.println("\nReplacement not possible: not enough stock.");
            System.out.println("Would you like to process as a refund instead?");
            System.out.println("1. Yes, process as REFUND");
            System.out.println("2. No, cancel the return");
            System.out.print("Enter option (1 or 2): ");

            String retryInput = scanner.nextLine().trim();
            switch (retryInput) {
                case "1" -> action = Return.ReturnAction.REFUND;
                case "2" -> {
                    System.out.println("Return canceled.");
                    AdminApp.sleepBriefly("Returning to User Menu...");
                    return;
                }
                default -> {
                    System.out.println("Invalid selection. Return canceled.");
                    AdminApp.sleepBriefly("Returning to User Menu...");
                    return;
                }
            }
        }

        System.out.print("Enter reason for return: ");
        String reason = scanner.nextLine();
        double unitPrice = selectedProduct.getPrice();

        ReturnsProcessor.processReturn(
            purchaseID,
            selectedProduct,
            quantity,
            action,
            reason,
            unitPrice,
            inventory,
            salesLog,
            returnLog
        );

        AdminApp.sleepBriefly("Return processed successfully.");
    }
}