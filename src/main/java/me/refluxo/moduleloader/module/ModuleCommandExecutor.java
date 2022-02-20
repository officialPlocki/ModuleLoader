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

    /**
     * This function is called when a command is executed
     *
     * @param sender The player who issued the command.
     * @param command The command that was executed.
     * @param label The name of the command.
     * @param args The arguments that were passed to the command.
     * @return A boolean value.
     */
    @Override
    public abstract boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args);

    /**
     * Returns a list of strings that are the possible completions for the last word of the command line
     *
     * @return A String list.
     */
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }

    /**
     * This function is called when a player clicks on an inventory
     *
     * @param event The event that was fired.
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        return;
    }

}
