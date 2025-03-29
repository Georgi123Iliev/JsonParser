package src.json.Parsing;

public class JsonParseResult {
    public Object parsedData;
    public String errorMessage;
    public int errorPosition;

    public JsonParseResult(Object parsedData) {
        this.parsedData = parsedData;
        this.errorMessage = null;
        this.errorPosition = -1;
    }

    public JsonParseResult(String errorMessage, int errorPosition) {
        this.parsedData = null;
        this.errorMessage = errorMessage;
        this.errorPosition = errorPosition;
    }

    public boolean isSuccess() {
        return parsedData != null;
    }
}
