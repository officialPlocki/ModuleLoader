package me.refluxo.moduleloader;

import me.refluxo.moduleloader.commands.ModuleCommand;
import me.refluxo.moduleloader.listeners.CommandListener;
import me.refluxo.moduleloader.module.ModuleManager;
import me.refluxo.moduleloader.util.files.FileBuilder;
import me.refluxo.moduleloader.util.files.YamlConfiguration;
import me.refluxo.moduleloader.util.mysql.CoinsAPI;
import me.refluxo.moduleloader.util.mysql.MySQLService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

public final class ModuleLoader extends JavaPlugin {

    // This is a static variable that holds the plugin instance.
    private static Plugin plugin;
    // This is a static variable that holds the module manager.
    private static ModuleManager mm;
    // This is a static variable that holds the plugin instance.
    private static ModuleLoader ml;
    // This is a static variable that holds the MySQL service.
    private static MySQLService service;

    /**
     * This function is called when the plugin is enabled
     */
    @Override
    public void onEnable() {
        ml = this;
        plugin = this;
        mm = new ModuleManager();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Objects.requireNonNull(getCommand("modules")).setExecutor(new ModuleCommand());
        Bukkit.getPluginManager().registerEvents(new CommandListener(), this);
        FileBuilder fb = new FileBuilder("config/library/config.yml");
        YamlConfiguration yml = fb.getYaml();
        if(!fb.getFile().exists()) {
            yml.set("mysql.host", "0.0.0.0");
            yml.set("mysql.port", 3306);
            yml.set("mysql.database", "database");
            yml.set("mysql.user", "user");
            yml.set("mysql.password", "password");
            fb.save();
            Bukkit.getPluginManager().disablePlugin(this);
        }
        service = new MySQLService();
        try {
            service.connect(
                yml.getString("mysql.host"),
                yml.getInt("mysql.port"),
                yml.getString("mysql.database"),
                yml.getString("mysql.user"),
                yml.getString("mysql.password")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CoinsAPI.init();
        mm.initializeAllModules();
    }

    @Override
    // This is a function that is called when the plugin is disabled.
    public void onDisable() {
        mm.unloadAllUnusedClasses();
        service.disconnect();
    }

    /**
     * Returns the module loader
     *
     * @return The module loader.
     */
    public static ModuleLoader getModuleLoader() {
        return ml;
    }

    /**
     * This function returns the module manager
     *
     * @return The singleton instance of the ModuleManager class.
     */
    public static ModuleManager getModuleManager() {
        return mm;
    }

    /**
     * This function returns the singleton instance of the MySQLService class
     *
     * @return The service object.
     */
    public static MySQLService getMySQLService() { return service; }

    /**
     * This function returns the plugin object
     *
     * @return The plugin object.
     */
    public static Plugin getPlugin() {
        return plugin;
    }

}
