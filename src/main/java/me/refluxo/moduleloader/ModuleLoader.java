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

    private static Plugin plugin;
    private static ModuleManager mm;
    private static ModuleLoader ml;
    private static MySQLService service;

    @Override
    public void onEnable() {
        ml = this;
        plugin = this;
        mm = new ModuleManager();
        mm.initializeAllModules();
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
        if(!MySQLService.isConnected()) {
            Bukkit.getPluginManager().disablePlugin(this);
        }

        CoinsAPI.init();
    }

    @Override
    public void onDisable() {
        mm.unloadAllUnusedClasses();
        service.disconnect();
    }

    public static ModuleLoader getModuleLoader() {
        return ml;
    }

    public static ModuleManager getModuleManager() {
        return mm;
    }

    public static MySQLService getMySQLService() { return service; }

    public static Plugin getPlugin() {
        return plugin;
    }

}
