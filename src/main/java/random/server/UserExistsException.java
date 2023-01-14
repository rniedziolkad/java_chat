package random.server;

public class UserExistsException extends Exception {
    public UserExistsException() {
        super("User with the given username already exists");
    }
}
