package src.json.types;

import src.exception.InvalidJsonPathException;
import src.exception.NotFoundException;

import java.util.Queue;

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

    String toJson(int padding);

    JsonArray search(String key);

    String extraNewline();

    void completePath(Queue<String> jsonPath) throws InvalidJsonPathException;

    JsonElement getValueAt(Queue<String> jsonPath, String previousKey) throws NotFoundException;

    String assign(Queue<String> jsonPath, String previousKey, JsonElement valueToAdd) throws NotFoundException;

}
