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
    private final MessageDigest hashFunction;
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.UTF_8);

    public UserManager(Connection dbConnection) throws SQLException{
        this.currentUser = null;
        this.loginStatement = dbConnection.prepareStatement("SELECT * FROM Users WHERE username = ? AND password_hash = ?");
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
