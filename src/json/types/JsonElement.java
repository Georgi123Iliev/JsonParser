package src.json.types;

/**
 * Represents a generic JSON element.
 */
public interface JsonElement {

    /**
     * Creates a deep copy of the JSON element.
     *
     * @return a new {@code JsonElement} that is structurally identical
     */
    JsonElement deepCopy();
}
