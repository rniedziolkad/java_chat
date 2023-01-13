public class AuthDataException extends Exception {
    public AuthDataException() {
        super("Incorrect username or password");
    }
}
