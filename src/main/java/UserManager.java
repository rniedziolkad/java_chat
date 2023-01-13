import org.sqlite.SQLiteException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {
    private String currentUser;
    private final PreparedStatement loginStatement;
    private final PreparedStatement registerStatement;
    private final PreparedStatement saltStatement;
    private final MessageDigest hashFunction;
    private final SecureRandom random;

    public UserManager(Connection dbConnection) throws SQLException{
        this.currentUser = null;
        String loginQuery = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";
        String registerQuery = "INSERT INTO Users VALUES (?, ?, ?)";
        String saltQuery = "SELECT salt FROM Users WHERE username = ?";
        this.loginStatement = dbConnection.prepareStatement(loginQuery);
        this.registerStatement = dbConnection.prepareStatement(registerQuery);
        this.saltStatement = dbConnection.prepareStatement(saltQuery);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Unable to create hash function");
        }
        this.hashFunction = md;
        this.random = new SecureRandom();
    }

    public void login(String username, String password) throws AuthDataException, SQLException {
        loginStatement.setString(1, username);
        byte [] salt = getUserSalt(username);
        loginStatement.setBytes(2, hashPassword(password, salt));

        ResultSet resultSet = loginStatement.executeQuery();
        if (!resultSet.next()){
            throw new AuthDataException();
        }
        this.currentUser = username;
    }
    public void register(String username, String password) throws UserExistsException, SQLException {
        registerStatement.setString(1, username);
        byte [] salt = generateSalt();
        registerStatement.setBytes(2, hashPassword(password, salt));
        registerStatement.setBytes(3, salt);

        try {
            registerStatement.executeUpdate();
            System.out.println("Registered new user: "+username);
        }catch (SQLiteException e){
            if (e.getResultCode().code == 1555)
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

    private byte [] hashPassword(String password, byte [] salt){
        if(hashFunction == null)
            return password.getBytes(StandardCharsets.UTF_8);

        if(salt != null)
            hashFunction.update(salt);
        return hashFunction.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    private byte [] generateSalt(){
        byte [] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private byte [] getUserSalt(String user) throws SQLException {
        saltStatement.setString(1, user);
        ResultSet resultSet = saltStatement.executeQuery();
        if(resultSet.next()) {
            System.out.println(resultSet);
            return resultSet.getBytes(1);
        }
        return null;
    }


}
