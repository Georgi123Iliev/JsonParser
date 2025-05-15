package src.json.types;

public class Nothing implements JsonElement {
    public static final Nothing INSTANCE = new Nothing();

    private Nothing() {}

    @Override
    public JsonElement deepCopy() {
        return this;
    }

    @Override
    public String toString() {
        return "<nothing>";
    }
}