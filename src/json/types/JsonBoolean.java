package src.json.types;

public class JsonBoolean implements JsonElement {
    private final Boolean value;

    public JsonBoolean(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public JsonElement deepCopy() {
        return new JsonBoolean(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}