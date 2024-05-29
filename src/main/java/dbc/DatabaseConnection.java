package dbc;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection databaseConnection;
    private Connection connection;

    private DatabaseConnection(){
        try(InputStream fileInputStream = DatabaseConnection.class.getClassLoader().getResourceAsStream("dbc.properties")) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            String driver = properties.getProperty("dbc.driver");
            String url = properties.getProperty("dbc.url");
            String user = properties.getProperty("dbc.user");
            String password =properties.getProperty("dbc.password");
            Class.forName(driver);
            connection = DriverManager.getConnection(url,user,password);
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (databaseConnection == null)
            databaseConnection = new DatabaseConnection();
        return databaseConnection;
    }

    public Connection getConnection(){
        return connection;
    }
}
