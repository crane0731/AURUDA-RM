package auruda.auruda.exception;

public class JwtCustomException extends RuntimeException {
    public JwtCustomException(String message) {
        super(message);
    }

  public JwtCustomException(String message, Throwable cause) {
    super(message, cause);
  }
}
