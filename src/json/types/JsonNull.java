package src.json.types;

public class JsonNull implements JsonElement {
    public static final JsonNull INSTANCE = new JsonNull();

    private JsonNull() {}

    @Override
    public JsonElement deepCopy() {
        return this;
    }

    @Override
    public String toString() {
        return "null";
    }
}