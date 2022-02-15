package me.refluxo.moduleloader.util.mysql;

import me.refluxo.moduleloader.ModuleLoader;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CoinsAPI {

    private final Player player;

    public CoinsAPI(Player player) {
        this.player = player;
        checkPlayer();
    }

    private void checkPlayer() {
        if(getCoins() == -1) {
            ModuleLoader.getMySQLService().executeUpdate("INSERT INTO coins(uuid,coins) VALUES ('" + player.getUniqueId() + "',0);");
        }
    }

    public int getCoins() {
        ResultSet rs = ModuleLoader.getMySQLService().getResult("SELECT * FROM coins WHERE uuid = '" + player.getUniqueId() + "';");
        try {
            if(rs.next()) {
                return rs.getInt("coins");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void removeCoins(int coins) {
        setCoins(getCoins()-coins);
    }

    public void addCoins(int coins) {
        setCoins(getCoins()+coins);
    }

    public void setCoins(int coins) {
        ModuleLoader.getMySQLService().executeUpdate("UPDATE coins SET coins = " + coins + " WHERE uuid = '" + player.getUniqueId() + "';");
    }

    public static void init() {
        ModuleLoader.getMySQLService().executeUpdate("CREATE TABLE IF NOT EXISTS coins(uuid TEXT, coins BIGINT);");
    }

}
