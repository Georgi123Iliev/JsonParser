package src.json.types;

/**
 * Represents a JSON number, backed by a {@link Number}.
 */
public class JsonNumber extends JsonPrimitive {
    private final Number value;

    /**
     * Constructs a {@code JsonNumber} with the given numeric value.
     *
     * @param value the numeric value to wrap
     */
    public JsonNumber(Number value) {
        this.value = value;
    }

    /**
     * @return the stored numeric value
     */
    public Number getValue() {
        return value;
    }

    /**
     * @return a new {@code JsonNumber} with the same value
     */
    @Override
    public JsonElement deepCopy() {
        return new JsonNumber(value);
    }

    @Override
    public String toJson(int padding) {
        return toString();
    }

    /**
     * @return string representation of the number
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
