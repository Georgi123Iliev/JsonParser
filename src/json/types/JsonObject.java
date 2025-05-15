package src.json.types;

import java.util.*;

public class JsonObject implements JsonElement {
    private final Map<String, JsonElement> members = new LinkedHashMap<>();

    public void add(String key, JsonElement value) {
        members.put(key, value);
    }

    public JsonElement get(String key) {
        return members.get(key);
    }

    public JsonElement remove(String key) {
        return members.remove(key);
    }

    public Set<String> keySet() {
        return members.keySet();
    }

    public boolean containsKey(String key) {
        return members.containsKey(key);
    }

    @Override
    public JsonElement deepCopy() {
        JsonObject copy = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : members.entrySet()) {
            copy.add(entry.getKey(), entry.getValue().deepCopy());
        }
        return copy;
    }

    @Override
    public String toString() {
        return members.toString();
    }
}