package me.refluxo.moduleloader.util.mysql;

import me.refluxo.moduleloader.ModuleLoader;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;

public class CoinsAPI {

    private final Player player;

    public CoinsAPI(Player player) {
        this.player = player;
        checkPlayer();
    }

    private void checkPlayer() {
        if(getCoins() == -1) {
            try (Connection connection = ModuleLoader.getMySQLService().getConnection()){
                var preparedStatement = connection.prepareStatement("INSERT INTO coins(uuid,coins) VALUES (?,0)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getCoins() {
        try (Connection connection = ModuleLoader.getMySQLService().getConnection()) {
            var preparedStatement = connection.prepareStatement("SELECT * FROM coins WHERE uuid = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            var rs = preparedStatement.executeQuery();
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
        try (Connection connection = ModuleLoader.getMySQLService().getConnection()) {
            var preparedStatement = connection.prepareStatement("UPDATE coins SET coins = ?  WHERE uuid =?");
            preparedStatement.setInt(1, coins);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        try {
            ModuleLoader.getMySQLService().getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS coins(uuid TEXT, coins BIGINT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
