import java.sql.Connection;

public class UserManager {
    private Connection dbConnection;
    private String currentUser;

    public UserManager(Connection dbConnection) {
        this.dbConnection = dbConnection;
        currentUser = null;
    }

    public void login(String username, String password) throws AuthDataException {

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
