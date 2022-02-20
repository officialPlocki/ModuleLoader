package me.refluxo.moduleloader.util.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

public class ClickableItemUtil {

    // This is a map that contains a map of maps. The outer map contains the inventory titles as keys and the inner map
    // contains the items as keys and the click actions as values.
    private static HashMap<String, HashMap<ItemStack, ClickAction>> clickActions;

    public ClickableItemUtil() {
        clickActions = new HashMap<>();
    }

    /**
     * Adds a click action to the clickActions map
     *
     * @param item The item that you want to add the action to.
     * @param inventoryTitle The title of the inventory.
     * @param action The action to be performed when the item is clicked.
     */
    public void addClickAction(ItemStack item, String inventoryTitle, ClickAction action) {
        HashMap<ItemStack, ClickAction> actions = clickActions.getOrDefault(inventoryTitle, new HashMap<>());
        actions.put(item, action);
        clickActions.put(inventoryTitle, actions);
    }

    /**
     * This class is used to listen to the click event of the inventory
     */
    static class ListenerClass implements Listener {

        /**
         * If the inventory title is equal to the title of the inventory we're looking for, then we iterate through the
         * items in the inventory and if the item we're looking for is equal to the item we're looking for, then we run the
         * action
         *
         * @param event The event that was fired.
         */
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
