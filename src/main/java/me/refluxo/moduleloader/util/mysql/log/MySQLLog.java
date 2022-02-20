package me.refluxo.moduleloader.util.mysql.log;

import java.sql.Connection;
import java.sql.SQLException;
import me.refluxo.moduleloader.ModuleLoader;
import org.bukkit.entity.Player;

public class MySQLLog {

    /**
     * It logs the player's name, the log message, and the current time
     *
     * @param player The player that did something
     * @param log The log message
     */
    public void log(Player player, String log) {
        try (Connection connection = ModuleLoader.getMySQLService().getConnection()) {
            var preparedStatement = connection.prepareStatement("INSERT INTO log(i,log,t) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "player." + player.getUniqueId());
            preparedStatement.setString(2, log);
            preparedStatement.setLong(3, System.currentTimeMillis());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts a log into the database
     *
     * @param index The index of the log.
     * @param log The log message
     */
    public void log(String index, String log) {
        try (Connection connection = ModuleLoader.getMySQLService().getConnection()) {
            var preparedStatement = connection.prepareStatement("INSERT INTO log(i,log,t) VALUES (?, ?, ?)");
            preparedStatement.setString(1, index);
            preparedStatement.setString(2, log);
            preparedStatement.setLong(3, System.currentTimeMillis());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
