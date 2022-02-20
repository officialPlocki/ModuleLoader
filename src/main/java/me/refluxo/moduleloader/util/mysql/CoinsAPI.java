package me.refluxo.moduleloader.util.mysql;

import me.refluxo.moduleloader.ModuleLoader;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.SQLException;

public class CoinsAPI {

    // This is a private final field that is used to store the player object.
    private final Player player;

    public CoinsAPI(Player player) {
        this.player = player;
        checkPlayer();
    }

    /**
     * This function checks if the player has a coins entry in the database. If they don't, it creates one
     */
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

    /**
     * It gets the player's coins from the database
     *
     * @return The coins of the player.
     */
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

    /**
     * Remove coins from the player's inventory
     *
     * @param coins The number of coins to remove.
     */
    public void removeCoins(int coins) {
        setCoins(getCoins()-coins);
    }

    /**
     * Add the given number of coins to the player's coins
     *
     * @param coins The number of coins to add to the player's coins.
     */
    public void addCoins(int coins) {
        setCoins(getCoins()+coins);
    }

    /**
     * This function sets the coins of a player to a specified value
     *
     * @param coins The amount of coins the player has.
     */
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

    /**
     * This function creates a table called coins if it doesn't exist, and if it does exist, it checks to see if the table
     * has the columns uuid and coins. If it doesn't, it adds the columns
     */
    public static void init() {
        try {
            ModuleLoader.getMySQLService().getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS coins(uuid TEXT, coins BIGINT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
