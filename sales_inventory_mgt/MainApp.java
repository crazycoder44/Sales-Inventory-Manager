import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Inventory inventory = new Inventory();
        List<Product> productCatalog = new ArrayList<>();
        List<Sale> salesLog = new ArrayList<>();
        List<Return> returnLog = new ArrayList<>();

        while (true) {
            AdminApp.sleepBriefly("\n\nWelcome to Tech-Hub Mart!");
            AdminApp.sleepBriefly("\nWhat would you like to do?");
            // System.out.println("\n=== TECH-HUB MART CONTROL CENTER ===");
            System.out.println("1. Admin Operations");
            System.out.println("2. User Operations");
            System.out.println("3. Exit App");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> AdminApp.runAdminMenu(scanner, inventory, productCatalog, salesLog, returnLog);
                case "2" -> OperationsApp.runUserMenu(scanner, inventory, productCatalog, salesLog, returnLog);
                case "3" -> {
                    AdminApp.sleepBriefly("Exiting Tech-Hub Mart...");
                    System.out.println("Goodbye!");
                    return;
                }
                default -> AdminApp.sleepBriefly("\nInvalid option. Please try again.");
            }
        }
    }
}