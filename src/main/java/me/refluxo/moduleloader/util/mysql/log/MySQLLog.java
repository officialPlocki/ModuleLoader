package me.refluxo.moduleloader.util.mysql.log;

import me.refluxo.moduleloader.ModuleLoader;
import org.bukkit.entity.Player;

public class MySQLLog {

    public void log(Player player, String log) {
        ModuleLoader.getMySQLService().executeUpdate("INSERT INTO log(i,log,t) VALUES ('player."+player.getUniqueId()+"','"+log+"','"+System.currentTimeMillis()+"')");
    }

    public void log(String index, String log) {
        ModuleLoader.getMySQLService().executeUpdate("INSERT INTO log(i,log,t) VALUES ('"+index+"','"+log+"','"+System.currentTimeMillis()+"')");
    }

}
