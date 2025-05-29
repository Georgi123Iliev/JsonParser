package src.exception;

public class InvalidJsonPathException extends RuntimeException {
  public InvalidJsonPathException(String message) {
    super(message);
  }
}
