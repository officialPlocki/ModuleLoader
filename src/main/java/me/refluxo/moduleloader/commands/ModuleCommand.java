package me.refluxo.moduleloader.commands;

import me.refluxo.moduleloader.ModuleLoader;
import me.refluxo.moduleloader.module.Module;
import me.refluxo.moduleloader.module.PluginModule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ModuleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("refluxo.module")) {
            if(args.length == 0) {
                sender.sendMessage("§b§lModules §8» §7Bitte benutze /modules <§elist§7, §einfo§7, §edisable§7, §eenable§7> <§eModule§7>");
            } else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage("§8» §b§lModules §8«");
                    sender.sendMessage("");
                    ModuleLoader.getModuleManager().getModules().forEach(module -> sender.sendMessage("§7» §b" + module.getClass().getAnnotation(Module.class).moduleName()));
                    sender.sendMessage("");
                } else {
                    sender.sendMessage("§b§lModules §8» §7Bitte benutze /modules <§elist§7, §einfo§7, §edisable§7, §eenable§7, §ecommand§7> <§eModul§7, §eCommand§7>");
                }
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("info")) {
                    String mName = args[1];
                    PluginModule module = ModuleLoader.getModuleManager().getModule(mName);
                    if(module != null) {
                        sender.sendMessage("§8» §b§lModules §8«");
                        sender.sendMessage("");
                        sender.sendMessage("§7Modul Name: §b" + module.getClass().getAnnotation(Module.class).moduleName());
                        sender.sendMessage("§7Befehle:");
                        for (me.refluxo.moduleloader.module.ModuleCommand moduleCommand : ModuleLoader.getModuleManager().getCommands(module)) {
                            sender.sendMessage("§7- §e" + moduleCommand.getClass().getAnnotation(me.refluxo.moduleloader.module.Command.class).command().toLowerCase());
                        }
                        sender.sendMessage("§7Berechtigungen:");
                        for (me.refluxo.moduleloader.module.ModuleCommand moduleCommand : ModuleLoader.getModuleManager().getCommands(module)) {
                            for(String permission : moduleCommand.getClass().getAnnotation(me.refluxo.moduleloader.module.Command.class).permissions()) {
                                sender.sendMessage("§7- §e" + permission);
                            }
                        }
                        sender.sendMessage("§7Listener: §e" + ModuleLoader.getModuleManager().getListeners(module).size());
                        sender.sendMessage("");
                    } else {
                        sender.sendMessage("§b§lModules §8» §7Das Modul wurde nicht gefunden.");
                    }
                } else if(args[0].equalsIgnoreCase("disable")) {
                    PluginModule module = ModuleLoader.getModuleManager().getModule(args[1]);
                    if(module != null) {
                        ModuleLoader.getModuleManager().disableModule(module);
                        sender.sendMessage("§b§lModules §8» §7Das Modul wurde deaktiviert.");
                    } else {
                        sender.sendMessage("§b§lModules §8» §7Das Modul wurde nicht gefunden.");
                    }
                } else if(args[0].equalsIgnoreCase("enable")) {
                    PluginModule module = ModuleLoader.getModuleManager().getModule(args[1]);
                    if(module != null) {
                        ModuleLoader.getModuleManager().enableModule(module);
                        sender.sendMessage("§b§lModules §8» §7Das Modul wurde aktiviert.");
                    } else {
                        sender.sendMessage("§b§lModules §8» §7Das Modul wurde nicht gefunden.");
                    }
                } else if(args[0].equalsIgnoreCase("command")) {
                    me.refluxo.moduleloader.module.ModuleCommand cmd = ModuleLoader.getModuleManager().getModuleCommand(args[1]);
                    if(cmd != null) {
                        me.refluxo.moduleloader.module.Command annotation = cmd.getClass().getAnnotation(me.refluxo.moduleloader.module.Command.class);
                        sender.sendMessage("§8» §b§lModules §8«");
                        sender.sendMessage("");
                        sender.sendMessage("§7TabComplete: §e" + annotation.tabCompleterIsEnabled());
                        sender.sendMessage("§7Befehl: §e" + annotation.command());
                        sender.sendMessage("§7Usage: §e" + annotation.usage());
                        sender.sendMessage("§7Beschreibung: §e" + annotation.description());
                        sender.sendMessage("§7Aliase:");
                        for (String alias : annotation.aliases()) {
                            sender.sendMessage("§7- §e" + alias);
                        }
                        sender.sendMessage("§7Berechtigungen:");
                        for(String permission : ModuleLoader.getModuleManager().getPermissions(cmd)) {
                            sender.sendMessage("§7- §e" + permission);
                        }
                        sender.sendMessage("");
                    } else {
                        sender.sendMessage("§b§lModules §8» §7Der Befehl konnte nicht gefunden werden.");
                    }
                } else {
                    sender.sendMessage("§b§lModules §8» §7Bitte benutze /modules <§elist§7, §einfo§7, §edisable§7, §eenable§7, §ecommand§7> <§eModul§7, §eCommand§7>");
                }
            } else {
                sender.sendMessage("§b§lModules §8» §7Bitte benutze /modules <§elist§7, §einfo§7, §edisable§7, §eenable§7, §ecommand§7> <§eModul§7, §eCommand§7>");
            }
        } else {
            sender.sendMessage("§b§lModules §8» §7Du hast dazu keine Rechte.");
        }
        return false;
    }

}
