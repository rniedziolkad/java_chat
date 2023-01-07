import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static void connect(){
        Connection connection;
        String dbUrl = "jdbc:sqlite:random.db";

        try {
            connection = DriverManager.getConnection(dbUrl);
            System.out.println("Connected to database");
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
