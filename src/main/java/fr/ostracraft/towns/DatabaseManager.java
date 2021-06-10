package fr.ostracraft.towns;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.ProxyConnection;
import fr.ostracraft.towns.types.DatabaseResponse;
import fr.ostracraft.towns.utils.Config;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        send("CREATE TABLE IF NOT EXISTS `" + Config.DB_PREFIX.get() + "towns`(`id` INT NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`), name TEXT, mayor TEXT, assistants TEXT DEFAULT '', members TEXT DEFAULT '', spawn TEXT DEFAULT '', rank TEXT DEFAULT 'CAMPEMENT', creation BIGINT);");
        send("CREATE TABLE IF NOT EXISTS `" + Config.DB_PREFIX.get() + "townblocks`(`id` INT NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`), `x` int, `z` int, `townId` int);");
        send("CREATE TABLE IF NOT EXISTS `" + Config.DB_PREFIX.get() + "residents`(`id` INT NOT NULL AUTO_INCREMENT, PRIMARY KEY (`id`), `uuid` TEXT, `username` TEXT, `townId` int);");
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

    public static DatabaseResponse get(String sql, Object... args) {
        HashMap<String, Object> hashMap = new HashMap<>();
        try {
            ProxyConnection connection = getConnection();
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                return null;
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                String name = resultSet.getMetaData().getColumnName(i + 1);
                hashMap.put(name, resultSet.getObject(name));
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return new DatabaseResponse(hashMap);
    }

    public static List<DatabaseResponse> getAll(String sql, Object... args) {
        List<DatabaseResponse> responses = new ArrayList<>();
        try {
            ProxyConnection connection = getConnection();
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    String name = resultSet.getMetaData().getColumnName(i + 1);
                    hashMap.put(name, resultSet.getObject(name));
                }
                responses.add(new DatabaseResponse(hashMap));
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return responses;
    }

    public static void send(String sql, Object... args) {
        try {
            ProxyConnection connection = getConnection();
            assert connection != null;
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            statement.execute();
            statement.close();
            connection.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

}
