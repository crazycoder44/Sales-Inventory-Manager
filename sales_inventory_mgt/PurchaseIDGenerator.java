import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public final class PurchaseIDGenerator {
    private static final Map<String, Integer> purchaseCountByDate = new HashMap<>();

    private PurchaseIDGenerator() {}

    public static String generatePurchaseID() {
        String dateKey = LocalDate.now().toString().replace("-", ""); // Format: YYYYMMDD
        int serial = purchaseCountByDate.getOrDefault(dateKey, 0) + 1;
        purchaseCountByDate.put(dateKey, serial);
        return "TXN_" + dateKey + "_" + String.format("%04d", serial);
    }
}