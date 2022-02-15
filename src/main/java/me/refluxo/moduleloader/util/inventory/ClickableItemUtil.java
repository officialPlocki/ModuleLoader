package me.refluxo.moduleloader.util.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

public class ClickableItemUtil {

    private static HashMap<String, HashMap<ItemStack, ClickAction>> clickActions;

    public ClickableItemUtil() {
        clickActions = new HashMap<>();
    }

    public void addClickAction(ItemStack item, String inventoryTitle, ClickAction action) {
        HashMap<ItemStack, ClickAction> actions = clickActions.getOrDefault(inventoryTitle, new HashMap<>());
        actions.put(item, action);
        clickActions.put(inventoryTitle, actions);
    }

    static class ListenerClass implements Listener {

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            clickActions.keySet().forEach(title -> {
                if(Objects.requireNonNull(event.getView().title().insertion()).equalsIgnoreCase(title)) {
                    HashMap<ItemStack, ClickAction> actions = clickActions.getOrDefault(title, new HashMap<>());
                    actions.forEach((item, action) -> {
                        if(event.getCurrentItem() != null) {
                            if(event.getCurrentItem().equals(item)) {
                                if(event.getCurrentItem().equals(item)) {
                                    action.action(event);
                                }
                            }
                        }
                    });
                }
            });
        }

    }

    interface ClickAction {
        void action(InventoryClickEvent event);
    }

}
