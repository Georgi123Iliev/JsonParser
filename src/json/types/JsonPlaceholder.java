package src.json.types;

public class JsonPlaceholder implements JsonElement {
    public static final JsonPlaceholder INSTANCE = new JsonPlaceholder();

    private JsonPlaceholder() {}

    @Override
    public JsonElement deepCopy() {
        return this;
    }

    @Override
    public String toString() {
        return "<placeholder>";
    }
}