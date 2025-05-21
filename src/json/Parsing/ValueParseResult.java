package src.json.Parsing;

import src.json.types.JsonElement;

/** Result wrapper for {@code ValueParser}. */
public class ValueParseResult {
    /** {@code true} if parsing succeeded. */
    public boolean isSuccess;
    /** Parsed value when successful, otherwise {@code null}. */
    public JsonElement parsedValue;

    /**
     * Constructs a successful result.
     *
     * @param parsedValue element produced by the parser
     */
    public ValueParseResult(JsonElement parsedValue) {
        isSuccess = true;
        this.parsedValue = parsedValue;
    }

    /**
     * Factory for an unsuccessful parse.
     *
     * @return instance with {@code isSuccess == false}
     */
    public static ValueParseResult FailedParse() {
        return new ValueParseResult();
    }

    /** Creates a failure instance. */
    private ValueParseResult() {
        isSuccess = false;
    }
}
