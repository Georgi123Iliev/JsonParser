package src.json.Parsing;

import src.json.types.JsonElement;

/** Holds either a parsed JSON tree or error details. */
public class JsonParseResult {
    /** Parsed root element when successful, otherwise {@code null}. */
    public JsonElement parsedData;
    /** Message describing the parse error, or {@code null} if none. */
    public String errorMessage;
    /** Offset in the input where the error occurred, or -1. */
    public int errorPosition;

    /**
     * Success constructor.
     *
     * @param parsedData root of the parsed JSON tree
     */
    public JsonParseResult(JsonElement parsedData) {
        this.parsedData = parsedData;
        this.errorMessage = null;
        this.errorPosition = -1;
    }

    /**
     * Failure constructor.
     *
     * @param errorMessage description of the issue
     * @param errorPosition index in the original text where it happened
     */
    public JsonParseResult(String errorMessage, int errorPosition) {
        this.parsedData = null;
        this.errorMessage = errorMessage;
        this.errorPosition = errorPosition;
    }

    /**
     * @return {@code true} if parsing finished without error
     */
    public boolean isSuccess() {
        return parsedData != null;
    }
}
