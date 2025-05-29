package src.json.types;

/**
 * Special placeholder used to signal absence of a value.
 */
public class Nothing extends JsonPrimitive {

    /** Shared singleton instance. */
    public static final Nothing INSTANCE = new Nothing();

    /** Private constructor to enforce singleton use. */
    private Nothing() {}

    /**
     * @return the singleton instance
     */
    @Override
    public JsonElement deepCopy() {
        return this;
    }

    @Override
    public String toJson(int padding) {
        return "";
    }

    /**
     * @return fixed string {@code "<nothing>"}
     */
    @Override
    public String toString() {
        return "<nothing>";
    }


}
