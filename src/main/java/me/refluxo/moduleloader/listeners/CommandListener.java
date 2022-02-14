package me.refluxo.moduleloader.listeners;

import me.refluxo.moduleloader.ModuleLoader;
import me.refluxo.moduleloader.module.ModuleCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String[] commandArgs = event.getBuffer().replaceFirst("/", "").replaceAll("-fallback", "").split(" ");
        ModuleCommand cmd = ModuleLoader.getModuleManager().getCommand(commandArgs[0]);
        if(cmd != null) {
            event.setCompletions(cmd.getTabCompletions(commandArgs));
        }
    }

    @EventHandler
    public void onProcess(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().replaceFirst("/", "").replaceAll("-fallback", "").split(" ")[0];
        if(!ModuleLoader.getModuleManager().canExecuted(command)) {
            event.setCancelled(true);
        }
        if(command.contains("bukkit:") ||
                command.equalsIgnoreCase("?") ||
                command.equalsIgnoreCase("about") ||
                command.equalsIgnoreCase("help") ||
                command.equalsIgnoreCase("pl") ||
                command.equalsIgnoreCase("plugins") ||
                command.equalsIgnoreCase("reload") ||
                command.equalsIgnoreCase("rl") ||
                command.equalsIgnoreCase("ver") ||
                command.equalsIgnoreCase("version") ||
                command.equalsIgnoreCase("minecraft:me") ||
                command.equalsIgnoreCase("me") ||
                command.equalsIgnoreCase("minecraft:help") ||
                command.equalsIgnoreCase("minecraft:kick") ||
                command.equalsIgnoreCase("minecraft:msg") ||
                command.equalsIgnoreCase("minecraft:ban") ||
                command.equalsIgnoreCase("minecraft:ban-ip") ||
                command.equalsIgnoreCase("minecraft:pardon") ||
                command.equalsIgnoreCase("minecraft:pardon-ip") ||
                command.equalsIgnoreCase("pardon-ip") ||
                command.equalsIgnoreCase("recipe") ||
                command.equalsIgnoreCase("ban-ip") ||
                command.equalsIgnoreCase("say") ||
                command.equalsIgnoreCase("minecraft:say") ||
                command.equalsIgnoreCase("tell") ||
                command.equalsIgnoreCase("minecraft:tell") ||
                command.equalsIgnoreCase("tellraw") ||
                command.equalsIgnoreCase("minecraft:tellraw")) {
            if(!event.getPlayer().hasPermission("modules.bypass")) {
                event.setCancelled(true);
            }
        }
        if(!Bukkit.getServer().getCommandMap().getKnownCommands().containsKey(command)) {
            event.setCancelled(true);
        }
        if(event.isCancelled()) {
            event.getPlayer().sendMessage("§b§lModules §8» §7Dieser Befehl existiert nicht.");
        }
    }

}
