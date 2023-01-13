import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {
    private String currentUser;
    private final PreparedStatement loginStatement;
    private final PreparedStatement registerStatement;
    private final MessageDigest hashFunction;
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.UTF_8);

    public UserManager(Connection dbConnection) throws SQLException{
        this.currentUser = null;
        String loginQuery = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";
        String registerQuery = "INSERT INTO Users VALUES (?, ?, ?)";
        this.loginStatement = dbConnection.prepareStatement(loginQuery);
        this.registerStatement = dbConnection.prepareStatement(registerQuery);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Unable to create hash function");
        }
        this.hashFunction = md;
    }

    public void login(String username, String password) throws AuthDataException, SQLException {
        loginStatement.setString(1, username);
        String passwordHash = bytesToHex(hashPassword(password));
        loginStatement.setBytes(2, hashPassword(password));

        System.out.println("username: "+username);
        System.out.println("hash: "+ passwordHash);

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

    private byte [] hashPassword(String password){
        if(hashFunction == null)
            return password.getBytes(StandardCharsets.UTF_8);

        return hashFunction.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    private String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
