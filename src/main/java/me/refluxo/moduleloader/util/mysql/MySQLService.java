package me.refluxo.moduleloader.util.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLService {

    // Creating a HikariDataSource object and setting the connection properties.
    private HikariDataSource hikariDataSource;

    /**
     * Create a HikariDataSource object and set the connection properties
     *
     * @param host The hostname of the database server.
     * @param port The port number of the MySQL server.
     * @param database The name of the database to connect to.
     * @param username The username to use when connecting to the database.
     * @param password The password for the database.
     */
    public void connect(String host, int port, String database, String username, String password) throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.setConnectionTimeout(1000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.hikariDataSource = new HikariDataSource(config);
    }

    /**
     * Get a connection from the connection pool
     *
     * @return Nothing.
     */
    public Connection getConnection(){
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This function takes a query as a parameter and executes it
     *
     * @param query The query to be executed.
     */
    public void executeUpdate(String query) {
        try (Connection connection = getConnection()){
            connection.prepareStatement(query).executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * It closes the HikariDataSource
     */
    public void disconnect() {
        hikariDataSource.close();
    }

}
