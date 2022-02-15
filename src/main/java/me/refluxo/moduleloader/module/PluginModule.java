package me.refluxo.moduleloader.module;

import me.refluxo.moduleloader.ModuleLoader;
import me.refluxo.moduleloader.util.mysql.MySQLService;
import org.bukkit.plugin.Plugin;

public abstract class PluginModule {

    public void enableModule() {}

    public void disableModule() {}

    public MySQLService getMySQLService() {
        return ModuleLoader.getMySQLService();
    }

    public ModuleLoader getModuleLoader() {
        return ModuleLoader.getModuleLoader();
    }

    public ModuleManager getModuleManager() {
        return ModuleLoader.getModuleManager();
    }

    public Plugin getPlugin() {
        return ModuleLoader.getPlugin();
    }

}