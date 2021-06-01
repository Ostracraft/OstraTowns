package fr.ostracraft.towns;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.ProxyConnection;
import fr.ostracraft.towns.utils.Config;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {

    private static HikariDataSource dataSource;
    private static final String DB_HOST = Config.DB_HOST.get();
    private static final int DB_PORT = Config.DB_PORT.get();
    private static final String DB_USER = Config.DB_USER.get();
    private static final String DB_PASSWORD = Config.DB_PASSWORD.get();
    private static final String DB_DATABASE = Config.DB_DATABASE.get();
    private static final boolean DB_SSL = Config.DB_SSL.get();

    public static void init() {
        /*
         * Connecting to the database
         */
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

        /*
         * Generating tables
         */
        ProxyConnection connection = getConnection();
        try (PreparedStatement townsStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + Config.DB_PREFIX.get() + "towns`(`id` INT NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`), name TEXT, mayor TEXT, assistants TEXT DEFAULT '', members TEXT DEFAULT '', spawn TEXT DEFAULT '', creation BIGINT);");
             PreparedStatement townBlocksStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + Config.DB_PREFIX.get() + "townblocks`(`x` int, `z` int, `townId` int);");
             PreparedStatement residentsStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + Config.DB_PREFIX.get() + "residents`(`uuid` TEXT, `username` TEXT, `townId` int);")
        ) {
            townsStatement.execute();
            townBlocksStatement.execute();
            residentsStatement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    @Nullable
    public static ProxyConnection getConnection() {
        if (dataSource == null)
            init();
        try {
            return (ProxyConnection) dataSource.getConnection();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

}
