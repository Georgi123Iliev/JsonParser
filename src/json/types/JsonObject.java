package src.json.types;

import java.util.*;

/**
 * Represents a JSON object, storing key-value pairs.
 */
public class JsonObject implements JsonElement {
    private final Map<String, JsonElement> members = new LinkedHashMap<>();

    /**
     * Adds or replaces a key-value pair in the object.
     *
     * @param key the key to add or replace
     * @param value the value associated with the key
     */
    public void add(String key, JsonElement value) {
        members.put(key, value);
    }

    /**
     * Retrieves the value associated with a given key.
     *
     * @param key the key to look up
     * @return the associated {@code JsonElement}, or {@code null} if not present
     */
    public JsonElement get(String key) {
        return members.get(key);
    }

    /**
     * Removes a key-value pair from the object.
     *
     * @param key the key to remove
     * @return the removed {@code JsonElement}, or {@code null} if not present
     */
    public JsonElement remove(String key) {
        return members.remove(key);
    }

    /**
     * @return a set of all keys in the object
     */
    public Set<String> keySet() {
        return members.keySet();
    }

    /**
     * Checks if the object contains a specific key.
     *
     * @param key the key to check
     * @return {@code true} if the key exists, otherwise {@code false}
     */
    public boolean containsKey(String key) {
        return members.containsKey(key);
    }

    /**
     * Creates a deep copy of the object.
     *
     * @return a new {@code JsonObject} with cloned entries
     */
    @Override
    public JsonElement deepCopy() {
        JsonObject copy = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : members.entrySet()) {
            copy.add(entry.getKey(), entry.getValue().deepCopy());
        }
        return copy;
    }

    /**
     * @return string representation of the object
     */
    @Override
    public String toString() {
        return members.toString();
    }
}