package me.refluxo.moduleloader.util.inventory;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class InventoryBuilder {

    // A property that is set once and cannot be changed.
    private final String title;
    // Setting the type of the inventory.
    private final InventoryType type;
    // This is a property that is set once and cannot be changed.
    private final int size;

    public InventoryBuilder(String displayName, int size) {
        this.size = size;
        title = displayName;
        type = null;
    }

    public InventoryBuilder(String displayName, InventoryType type) {
        this.size = 0;
        title = displayName;
        this.type = type;
    }

    /**
     * This function creates an inventory of the given size and title
     *
     * @param items A map of slots to items.
     * @return The inventory.
     */
    public Inventory buildInventory(Map<Integer, ItemStack> items) {
        if(type==null) {
            Inventory inv = Bukkit.createInventory(null, size, title);
            for(int i : items.keySet()) {
                inv.setItem(i, items.get(i));
            }
            return inv;
        } else {
            Inventory inv = Bukkit.createInventory(null, size, title);
            for(int i : items.keySet()) {
                inv.setItem(i, items.get(i));
            }
            return inv;
        }
    }

}
