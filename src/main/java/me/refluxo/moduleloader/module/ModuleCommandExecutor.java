package me.refluxo.moduleloader.module;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ModuleCommandExecutor implements CommandExecutor, Listener {

    @Override
    public abstract boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args);

    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {}

}
