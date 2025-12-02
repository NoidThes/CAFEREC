package ph.edu.dlsu.lbycpei.caferecommmendationsystem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Receipt {

    public static String getReceiptText(Order order) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();

        sb.append("\n===============================\n");
        sb.append("          CAFE RECEIPT         \n");
        sb.append("===============================\n");
        sb.append("Date: ").append(dtf.format(LocalDateTime.now())).append("\n");
        sb.append("\n");

        Map<String, Integer> itemCounts = new HashMap<>();
        Map<String, Double> itemPrices = new HashMap<>();
        
        double total = 0;

        for (MenuItem item : order.getItems()) {
            sb.append(String.format("%-25s ₱%.2f%n", item.getName(), item.getPrice()));
            total += item.getPrice();
        }

        for (String name : itemCounts.keySet()) {
            int quantity = itemCounts.get(name);
            double price = itemPrices.get(name);
            sb.append(String.format("%-20s x%-3d ₱%.2f%n", name, quantity, price * quantity));
            total += price * quantity;
        }


        sb.append("-------------------------------\n");
        sb.append(String.format("%-25s ₱%.2f%n", "TOTAL:", total));
        sb.append("===============================\n");
        sb.append("     Thank you for ordering!   \n");
        sb.append("===============================\n");

        return sb.toString();
    }

}
