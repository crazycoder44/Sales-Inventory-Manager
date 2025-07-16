import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public final class SalesProcessor {

    private SalesProcessor() {} // Utility class — no instantiation

    // Inner helper class to store selected items
    public static class ProductSelection {
        public final Product product;
        public final int quantity;

        public ProductSelection(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    // STEP 1: Build shopping basket through product selection
    public static List<ProductSelection> buildSaleBasket(Scanner scanner, List<Product> productCatalog, Inventory inventory) {
        List<ProductSelection> basket = new ArrayList<>();
        boolean keepShopping = true;

        while (keepShopping) {
            System.out.println("\nAvailable Products:");
            for (int i = 0; i < productCatalog.size(); i++) {
                Product p = productCatalog.get(i);
                System.out.println((i + 1) + ". " + p.getName() + " - $" + p.getPrice()
                    + " (" + inventory.getStockLevel(p) + " units in stock)");
            }

            Product selectedProduct = null;
            while (selectedProduct == null) {
                System.out.print("Select product by number: ");
                int choice;
                try {
                    choice = Integer.parseInt(scanner.nextLine()) - 1;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid choice. Try again.");
                    continue;
                }

                if (choice < 0 || choice >= productCatalog.size()) {
                    System.out.println("Invalid choice. Try again.");
                } else {
                    selectedProduct = productCatalog.get(choice);
                }
            }

            int quantity = -1;
            while (true) {
                System.out.print("Enter quantity to buy: ");
                String input = scanner.nextLine();

                try {
                    quantity = Integer.parseInt(input);

                    if (quantity < 0) {
                        throw new IllegalArgumentException("negative");
                    }

                    if (quantity == 0) {
                        System.out.println("Quantity cannot be zero.");
                    } else {
                        break;
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Quantity must be a positive number.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Quantity cannot be negative.");
                }
            }

            if (inventory.getStockLevel(selectedProduct) < quantity) {
                System.out.println("Not enough stock for " + selectedProduct.getName());
                continue;
            }

            basket.add(new ProductSelection(selectedProduct, quantity));

            while (true) {
                System.out.println("\nWould you like to buy another item?");
                System.out.println("1. Yes");
                System.out.println("2. No");
                System.out.print("Enter option (1 or 2): ");

                String choiceInput = scanner.nextLine().trim();

                if (choiceInput.equals("1")) {
                    keepShopping = true;
                    break;
                } else if (choiceInput.equals("2")) {
                    keepShopping = false;
                    break;
                } else {
                    System.out.println("Invalid selection. Please enter 1 for Yes or 2 for No.");
                }
            }

        }

        return basket;
    }

    // STEP 2: Finalize the purchase — apply role pricing & record sales
    public static void finalizePurchase(
        List<ProductSelection> basket,
        String buyerRole,
        Inventory inventory,
        List<Sale> salesLog
    ) {
        double multiplier = switch (buyerRole.toLowerCase()) {
            case "junior developer" -> {
                AdminApp.sleepBriefly("\nYou're poor! You get 20% off.");
                yield 0.8;
            }
            case "senior developer" -> {
                AdminApp.sleepBriefly("\nYou're rich! You get 25% added to your price to fund discount for poor people.");
                yield 1.25;
            }
            case "cto" -> {
                AdminApp.sleepBriefly("\nYou're super rich! You get 50% added to your price to help the poor.");
                yield 1.5;
            }
            default -> {
                System.out.println("Unknown role. Base pricing applied.");
                yield 1.0;
            }
        };

        String purchaseID = PurchaseIDGenerator.generatePurchaseID();
        double totalPaid = 0;

        for (ProductSelection item : basket) {
            Product product = item.product;
            int quantity = item.quantity;

            double baseAmount = product.getPrice() * quantity;
            double finalAmount = baseAmount * multiplier;

            boolean success = inventory.deductStock(product, quantity);
            if (!success) {
                System.out.println("Inventory deduction failed for " + product.getName());
                continue;
            }

            Sale sale = new Sale(product, quantity, finalAmount, purchaseID);
            salesLog.add(sale);
            totalPaid += finalAmount;
        }

        // Display grouped receipt
        System.out.println("\nPURCHASE RECEIPT - ID: " + purchaseID);
        for (Sale s : salesLog) {
            if (s.getPurchaseID().equals(purchaseID)) {
                System.out.println(s.getProduct().getName() + " - $" + s.getProduct().getPrice()
                    + " x " + s.getQuantitySold() + " = $" + s.getSaleAmount());
            }
        }

        System.out.println("Total Paid: $" + totalPaid);
        System.out.println("Thank you for shopping at Tech-Hub Mart!\n");
    }
}