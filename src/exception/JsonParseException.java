package src.exception;

public class JsonParseException extends Exception{
    private final int position;

    public JsonParseException(String message, int position) {
        super(message);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}

