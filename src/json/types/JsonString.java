package src.json.types;

/**
 * Represents a JSON string value.
 */
public class JsonString extends JsonPrimitive {
    private final String value;

    /**
     * Constructs a {@code JsonString} with the specified value.
     *
     * @param value the string to store
     */
    public JsonString(String value) {
        this.value = value;
    }

    /**
     * @return the wrapped string value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return a new {@code JsonString} with the same value
     */
    @Override
    public JsonElement deepCopy() {
        return new JsonString(value);
    }

    @Override
    public String toJson(int padding) {

        // За примитиви няма значение indent-а
        // Той е нужен само за по-сложните типове
        return toString();
    }

    /**
     * @return the JSON-style quoted string
     */
    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
