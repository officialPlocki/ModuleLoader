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
    private final String displayName;
    private Material material;
    private final List<String> lore = new ArrayList<>();
    private ItemStack item;
    private int amount = 1;
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

    public ItemUtil setAmount(int count) {
        amount = count;
        return this;
    }

    public ItemUtil setEnchanted(boolean enabled) {
        enchanted = enabled;
        return this;
    }

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
