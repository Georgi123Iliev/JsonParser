package src.json.types;

import src.exception.InvalidJsonPathException;
import src.exception.NotFoundException;

import java.util.*;

/**
 * Represents a JSON object, storing key-value pairs.
 */
public class JsonObject extends JsonComposite {
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

    @Override
    public String toJson(int indent) {
        StringBuilder sb = new StringBuilder();

        String padding = "\t".repeat(Math.max(0, indent));
        String biggerPadding =  "\t".repeat(indent+1);

        sb.append("\n").append(padding).append("{\n");

        int i = 0;
        for (var key  : members.keySet()) {

            sb.append(biggerPadding).append("\"").append(key).append("\"").append(" : ").
                    append(members.get(key).toJson(indent+1));
            if(i != members.size()-1)
                sb.append(",");

            sb.append("\n");
            i++;
        }
        sb.append(padding).append("}");
        return sb.toString();

    }

    @Override
    public JsonArray search(String key) {

        JsonArray result = new JsonArray();
        for (String k : members.keySet()) {

            if (k.equals(key)) {
                JsonElement value = members.get(k);
                result.add(value);
            }
            result.addAll(members.get(k).search(key));
        }
        return result;
    }

    @Override
    public void completePath(Queue<String> jsonPath) throws InvalidJsonPathException {

        String head = jsonPath.poll();
        String next = jsonPath.peek();

        if (head == null) {
            return;
        }
        JsonElement nextElement = (next == null) ? JsonNull.INSTANCE : (next.matches("\\[\\d+\\]") ? new JsonArray() : new JsonObject());

        if (head.matches("\\[\\d+\\]")) {
            throw new InvalidJsonPathException("Unexpected array index '" + head + "' in object path");
        }

        if (!containsKey(head)) {
            add(head, nextElement);
        }


        get(head).completePath(jsonPath);

    }

    @Override
    public JsonElement getValueAt(Queue<String> jsonPath, String previousKey) throws NotFoundException {
        String head = jsonPath.poll();

        if(head == null)
            return this;

        if (!containsKey(head)) {
            throw new NotFoundException("JSON object '" + previousKey + "' has no key '" + head + "'");
        }

        JsonElement element = get(head);
        return element.getValueAt(jsonPath, head);



    }

    @Override
    public String assign(Queue<String> jsonPath, String previousKey, JsonElement valueToAdd) throws NotFoundException {


        if (jsonPath.isEmpty()) {
            return "Empty JSON Path";
        }

        String head = jsonPath.poll();


        if (!containsKey(head)) {
            return "Json path error: '" + previousKey + "' has no key '" + head + "'";
        }

        if (jsonPath.isEmpty()) {
            if (valueToAdd instanceof Nothing) {
                remove(head);
                return "Element removed";
            } else {
                add(head, valueToAdd);
                return "Element set";
            }
        }

        return get(head).assign(jsonPath, head, valueToAdd);


    }

    /**
     * @return string representation of the object
     */
    @Override
    public String toString() {
        return members.toString();
    }
}