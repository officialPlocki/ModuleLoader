package me.refluxo.moduleloader.module;

import me.refluxo.moduleloader.ModuleLoader;
import me.refluxo.moduleloader.util.mysql.MySQLService;
import org.bukkit.plugin.Plugin;

public abstract class PluginModule {

    public void enableModule() {}

    public void disableModule() {}

    public static MySQLService getMySQLService() {
        return ModuleLoader.getMySQLService();
    }

    public static ModuleLoader getModuleLoader() {
        return ModuleLoader.getModuleLoader();
    }

    public static ModuleManager getModuleManager() {
        return ModuleLoader.getModuleManager();
    }

    public static Plugin getPlugin() {
        return ModuleLoader.getPlugin();
    }

}