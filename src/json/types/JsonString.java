package src.json.types;

public class JsonString implements JsonElement {
    private final String value;

    public JsonString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public JsonElement deepCopy() {
        return new JsonString(value);
    }

    @Override
    public String toString() {
        return "\""+ value +"\"";
    }
}