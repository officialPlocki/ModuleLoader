package me.refluxo.moduleloader.util.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLService {

    private static Connection con;

    public void connect(String host, int port, String database, String username, String password) throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        con = new HikariDataSource(config).getConnection();
    }

    public void disconnect() {
        if(isConnected()) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isConnected() {
        try {
            return !con.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void executeUpdate(String sql) {
        try {
            con.prepareStatement(sql).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getResult(String sql) {
        try {
            return con.prepareStatement(sql).executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
