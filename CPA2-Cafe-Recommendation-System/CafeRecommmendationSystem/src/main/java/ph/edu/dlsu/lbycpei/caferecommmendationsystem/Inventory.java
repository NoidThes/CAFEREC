package ph.edu.dlsu.lbycpei.caferecommmendationsystem;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private List<InventoryItem> items = new ArrayList<>();

    public void addItem(InventoryItem item) {
        items.add(item);
    }

    public List<InventoryItem> getItems() {
        return items;
    }

    public InventoryItem getItemByName(String name) {
        for (InventoryItem it : items) {
            if (it.getName().equalsIgnoreCase(name))
                return it;
        }
        return null;
    }

    public boolean reduceStock(String itemName) {
        InventoryItem item = getItemByName(itemName);
        if (item == null) return false;

        if (item.getQuantity() > 0) {
            item.setQuantity(item.getQuantity() - 1);
            return true;
        }
        return false;
    }
}
