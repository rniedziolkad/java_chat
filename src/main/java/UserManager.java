import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {
    private String currentUser;
    private final PreparedStatement loginStatement;

    public UserManager(Connection dbConnection) throws SQLException{
        this.currentUser = null;
        this.loginStatement = dbConnection.prepareStatement("SELECT * FROM Users WHERE username = ? AND password_hash = ?");
    }

    public void login(String username, String password) throws AuthDataException, SQLException {
        loginStatement.setString(1, username);
        loginStatement.setString(2, password);
        ResultSet resultSet = loginStatement.executeQuery();
        if (!resultSet.next()){
            throw new AuthDataException();
        }
        System.out.println(resultSet.getString(1)+ " "+resultSet.getString(2));
        this.currentUser = username;
    }
    public void register(String username, String password) throws UserExistsException{

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
