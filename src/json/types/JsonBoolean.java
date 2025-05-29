package src.json.types;

/**
 * Represents a JSON boolean value.
 */
public class JsonBoolean extends JsonPrimitive {
    private final Boolean value;

    /**
     * Constructs a {@code JsonBoolean} with the given value.
     *
     * @param value the boolean value to wrap
     */
    public JsonBoolean(Boolean value) {
        this.value = value;
    }

    /**
     * @return the stored boolean value
     */
    public Boolean getValue() {
        return value;
    }

    /**
     * @return a new {@code JsonBoolean} with the same value
     */
    @Override
    public JsonElement deepCopy() {
        return new JsonBoolean(value);
    }

    @Override
    public String toJson(int padding) {
        return toString();
    }

    /**
     * @return string representation of the boolean value
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
