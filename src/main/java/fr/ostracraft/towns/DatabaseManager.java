package fr.ostracraft.towns;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.ProxyConnection;
import fr.ostracraft.towns.utils.Config;

import java.sql.SQLException;

public class DatabaseManager {

    private static HikariDataSource dataSource;
    private static final String DB_HOST = Config.DB_HOST.getAsString();
    private static final String DB_PORT = Config.DB_PORT.getAsString();
    private static final String DB_USER = Config.DB_USER.getAsString();
    private static final String DB_PASSWORD = Config.DB_PASSWORD.getAsString();
    private static final String DB_DATABASE = Config.DB_DATABASE.getAsString();
    private static final String DB_SSL = Config.DB_SSL.getAsString();

    public static void init() {
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" +
                DB_HOST +
                ":" +
                DB_PORT +
                "/" +
                DB_DATABASE +
                "?useSSL=" +
                DB_SSL
        );
        dataSource.setUsername(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
    }

    public static ProxyConnection getConnection() throws SQLException {
        if(dataSource == null)
            init();
        return (ProxyConnection) dataSource.getConnection();
    }

}
