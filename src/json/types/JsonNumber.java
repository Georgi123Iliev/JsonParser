package src.json.types;

public class JsonNumber implements JsonElement {
    private final Number value;

    public JsonNumber(Number value) {
        this.value = value;
    }

    public Number getValue() {
        return value;
    }

    @Override
    public JsonElement deepCopy() {
        return new JsonNumber(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}