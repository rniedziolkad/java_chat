import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {
    private String currentUser;
    private final PreparedStatement loginStatement;
    private final PreparedStatement registerStatement;

    public UserManager(Connection dbConnection) throws SQLException{
        this.currentUser = null;
        String loginQuery = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";
        String registerQuery = "INSERT INTO Users VALUES (?, ?)";
        this.loginStatement = dbConnection.prepareStatement(loginQuery);
        this.registerStatement = dbConnection.prepareStatement(registerQuery);
    }

    public void login(String username, String password) throws AuthDataException, SQLException {
        loginStatement.setString(1, username);
        loginStatement.setString(2, password);
        ResultSet resultSet = loginStatement.executeQuery();
        if (!resultSet.next()){
            throw new AuthDataException();
        }
        this.currentUser = username;
    }
    public void register(String username, String password) throws UserExistsException, SQLException {
        registerStatement.setString(1, username);
        registerStatement.setString(2, password);
        try {
            registerStatement.executeUpdate();
            System.out.println("Registered new user: "+username);
        }catch (SQLException e){
            if (e.getErrorCode() == 19)
                throw new UserExistsException();

            throw e;
        }
    }
    public void logout(){
        currentUser = null;
    }

    public boolean isUserLoggedIn(){
        return currentUser != null;
    }

    public String getCurrentUser() {
        return currentUser;
    }
}
