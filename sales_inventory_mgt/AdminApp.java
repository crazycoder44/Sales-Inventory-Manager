import java.util.List;
import java.util.Scanner;

public final class AdminApp {

    public static void runAdminMenu(Scanner scanner, Inventory inventory, List<Product> productCatalog, List<Sale> salesLog, List<Return> returnLog) {
        while (true) {
            System.out.println("\n=== ADMIN PANEL ===");
            System.out.println("1. Create Product");
            System.out.println("2. Modify Product Price");
            System.out.println("3. Delete Product");
            System.out.println("4. View Products");
            System.out.println("5. Add Product to Inventory");
            System.out.println("6. View Inventory");
            System.out.println("7. View Sales Log");
            System.out.println("8. View Return Log");
            System.out.println("9. Exit to Main Menu");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    String name = "";

                    while (true) {
                        System.out.print("Enter product name: ");
                        name = scanner.nextLine().trim();

                        final String nameCheck = name;

                        boolean nameExists = productCatalog.stream()
                            .anyMatch(p -> p.getName().equalsIgnoreCase(nameCheck));

                        if (nameExists) {
                            System.out.println("A product with this name already exists!");
                        } else if (name.isEmpty()) {
                            System.out.println("Product name cannot be empty.");
                        } else {
                            break; // name is valid and unique
                        }
                    }
                    
                    double price = promptForPrice(scanner, "Enter product price: ");

                    Product p = new Product(name, price);
                    productCatalog.add(p);
                    System.out.println("Product created!");
                    sleepBriefly("Returning to Admin Menu...");
                }
                case "2" -> {
                    if (productCatalog.isEmpty()) {
                        System.out.println("No products available to update.");
                        sleepBriefly("Returning to Admin Menu...");
                        break;
                    }

                    Product selectedProduct = promptForProductSelection(scanner, productCatalog, "update");

                    double newPrice = promptForPrice(scanner, "Enter new price for " + selectedProduct.getName() + ": ");

                    selectedProduct.setPrice(newPrice);
                    System.out.println("Price for " + selectedProduct.getName() + " updated to $" + newPrice);
                    sleepBriefly("Returning to Admin Menu...");
                }
                case "3" -> {
                    if (productCatalog.isEmpty()) {
                        System.out.println("No products available to delete.");
                        sleepBriefly("Returning to Admin Menu...");
                        break;
                    }

                    Product selectedProduct = promptForProductSelection(scanner, productCatalog, "update");

                    final Product productToDelete = selectedProduct;

                    boolean isInStock = inventory.isInInventory(productToDelete)
                        && inventory.getStockLevel(selectedProduct) > 0;

                    boolean hasBeenSold = salesLog.stream()
                        .anyMatch(sale -> sale.getProduct().equals(productToDelete));

                    if (isInStock || hasBeenSold) {
                        System.out.println("Cannot delete product '" + productToDelete.getName() +
                            "' because it is either still in stock or has been sold.");
                    } else {
                        productCatalog.remove(productToDelete);
                        System.out.println("Product '" + productToDelete.getName() + "' successfully removed from catalog.");
                    }
                    sleepBriefly("Returning to Admin Menu...");
                }
                case "4" -> {
                    if (productCatalog.isEmpty()) {
                        System.out.println("No products available in the catalog.");
                        sleepBriefly("Returning to Admin Menu...");
                        break;
                    }

                    System.out.println("\n Product Catalog:");
                    for (Product p : productCatalog) {
                        System.out.println("- " + p.getName() + ": $" + p.getPrice());
                    }
                    sleepBriefly("Returning to Admin Menu...");
                }

                case "5" -> {
                    if (productCatalog.isEmpty()) {
                        System.out.println("No products available in the catalog.");
                        sleepBriefly("Returning to Admin Menu...");
                        break;
                    }

                    Product selectedProduct = promptForProductSelection(scanner, productCatalog, "update");

                    final Product productToAdd = selectedProduct;

                    int quantity = SalesProcessor.promptForQuantity(scanner, "Enter quantity to add to inventory for " + productToAdd.getName() + ": ");

                    inventory.addStock(productToAdd, quantity);
                    System.out.println("Added " + quantity + " unit(s) of " + productToAdd.getName() + " to inventory.");
                    sleepBriefly("Returning to Admin Menu...");

                }

                case "6" -> inventory.displayInventory();

                case "7" -> {
                    if (salesLog.isEmpty()) {
                        System.out.println("\nNo sales recorded.");
                        sleepBriefly("Returning to Admin Menu...");
                    } else {
                        System.out.println("\n SALES LOG:");
                        for (Sale sale : salesLog) {
                            System.out.println("- [" + sale.getPurchaseID() + "] "
                                + sale.getProduct().getName() + " x " + sale.getQuantitySold()
                                + " = $" + sale.getSaleAmount() + " @ " + sale.getTimestamp());
                        }
                    }
                }

                case "8" -> {
                    if (returnLog.isEmpty()) {
                        System.out.println("\nNo returns recorded.");
                        sleepBriefly("Returning to Admin Menu...");
                    } else {
                        System.out.println("\n RETURN LOG:");
                        for (Return ret : returnLog) {
                            System.out.println("- [" + ret.getPurchaseID() + "] "
                                + ret.getProduct().getName() + " x " + ret.getQuantityReturned()
                                + " | " + ret.getAction()
                                + " | Reason: " + ret.getReason()
                                + " | Refund: $" + ret.getRefundAmount());
                        }
                    }
                }

                case "9" -> {
                    sleepBriefly("Returning to Main Menu...");
                    return;
                }
                default -> sleepBriefly("\nInvalid option. Please try again.");
            }
        }
    }

    // Reusable method to prompt and validate price input
    public static double promptForPrice(Scanner scanner, String promptMessage) {
        double price;

        while (true) {
            System.out.print(promptMessage);
            String input = scanner.nextLine();

            try {
                price = Double.parseDouble(input);

                if (price < 0) {
                    throw new IllegalArgumentException("negative");
                }

                if (price == 0) {
                    System.out.println("Product price cannot be zero.");
                } else {
                    return price; // valid price
                }

            } catch (NumberFormatException e) {
                System.out.println("Product price must be a positive number.");
            } catch (IllegalArgumentException e) {
                System.out.println("Product price cannot be negative.");
            }
        }
    }

    // Reusable method to prompt user to select a product
    public static Product promptForProductSelection(Scanner scanner, List<Product> productCatalog, String action) {
        System.out.println("\nSelect a product to " + action + ":");
        for (int i = 0; i < productCatalog.size(); i++) {
            Product p = productCatalog.get(i);
            System.out.println((i + 1) + ". " + p.getName() + " ($" + p.getPrice() + ")");
        }

        while (true) {
            System.out.print("Enter option number: ");
            try {
                int selection = Integer.parseInt(scanner.nextLine()) - 1;

                if (selection < 0 || selection >= productCatalog.size()) {
                    System.out.println("Invalid selection. Please choose a valid product number.");
                } else {
                    return productCatalog.get(selection);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    // Reusable method to pause execution briefly
    // with an optional message
    public static void sleepBriefly(String message) {
        try {
            Thread.sleep(1000);
            if (message != null) {
                System.out.println(message);
                Thread.sleep(1000);
            }
        } catch (InterruptedException ignored) {}
    }
}
