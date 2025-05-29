package src.json.types;

/**
 * Represents a JSON {@code null} value (singleton).
 */
public class JsonNull extends JsonPrimitive {

    /** Shared instance of {@code JsonNull}. */
    public static final JsonNull INSTANCE = new JsonNull();

    /** Private constructor to enforce singleton pattern. */
    private JsonNull() {}

    /**
     * @return this singleton instance
     */
    @Override
    public JsonElement deepCopy() {
        return this;
    }

    @Override
    public String toJson(int padding) {
        return toString();
    }


    /**
     * @return the string {@code "null"}
     */
    @Override
    public String toString() {
        return "null";
    }
}
