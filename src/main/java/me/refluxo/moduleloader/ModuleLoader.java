package me.refluxo.moduleloader;

import me.refluxo.moduleloader.commands.ModuleCommand;
import me.refluxo.moduleloader.util.ModuleManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ModuleLoader extends JavaPlugin {

    private static Plugin plugin;
    private static ModuleManager mm;
    private static ModuleLoader ml;

    @Override
    public void onEnable() {
        // Plugin startup logic
        ml = this;
        plugin = this;
        mm = new ModuleManager();
        mm.initializeAllModules();
        Objects.requireNonNull(getCommand("modules")).setExecutor(new ModuleCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        mm.unloadAllUnusedClasses();
    }

    public static ModuleLoader getModuleLoader() {
        return ml;
    }

    public static ModuleManager getModuleManager() {
        return mm;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

}
