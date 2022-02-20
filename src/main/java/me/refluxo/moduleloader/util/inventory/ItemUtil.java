package me.refluxo.moduleloader.util.inventory;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ItemUtil {
    // A private final field that holds the display name of the item.
    private final String displayName;
    // A private field that holds the material of the item.
    private Material material;
    // This is a list of strings that will be added to the item's lore.
    private final List<String> lore = new ArrayList<>();
    // A private field that holds the itemstack of the item.
    private ItemStack item;
    // This is a private field that holds the amount of the item.
    private int amount = 1;
    // This is a flag that tells the program whether or not the item should be enchanted.
    boolean enchanted = false;

    public ItemUtil(final String displayName, ItemStack item, final String... lore) {
        this.displayName = displayName;
        this.item = item;
        this.lore.addAll(Arrays.asList(lore));
    }

    public ItemUtil(final String displayName, final Material material, final String... lore) {
        this.displayName = displayName;
        this.material = material;
        this.lore.addAll(Arrays.asList(lore));
    }

    /**
     * This function sets the amount of the item
     *
     * @param count The amount of the item to be added.
     * @return Nothing.
     */
    public ItemUtil setAmount(int count) {
        amount = count;
        return this;
    }

    /**
     * This function sets the boolean variable "enchanted" to the value of the parameter "enabled"
     *
     * @param enabled Whether the item is enchanted or not.
     * @return Nothing.
     */
    public ItemUtil setEnchanted(boolean enabled) {
        enchanted = enabled;
        return this;
    }

    /**
     * This function creates an ItemStack object from the given material, and sets the display name and lore of the item
     *
     * @return The ItemStack that is being built.
     */
    public ItemStack buildItem() {
        ItemStack itemstack = Objects.requireNonNullElseGet(item, () -> new ItemStack(this.material));
        final ItemMeta itemMeta = itemstack.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        if(enchanted) {
            itemMeta.addEnchant(Enchantment.DURABILITY,0,true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemstack.setItemMeta(itemMeta);
        itemstack.setAmount(amount);
        return itemstack;
    }
}
