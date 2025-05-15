package src.json.types;

import java.util.*;

public class JsonArray implements JsonElement {
    private final List<JsonElement> elements;

    public JsonArray(List<JsonElement> elements) {
        this.elements = elements;
    }

    public JsonArray() {
        elements = new ArrayList<>();
    }

    public void add(JsonElement element) {
        elements.add(element);
    }

    public JsonElement get(int index) {
        return elements.get(index);
    }

    public boolean isEmpty()
    {
        return elements.isEmpty();
    }

    public void addAll(JsonArray other)
    {
        this.elements.addAll(other.elements);
    }
    public int size() {
        return elements.size();
    }

    @Override
    public JsonElement deepCopy() {
        JsonArray copy = new JsonArray();
        for (JsonElement el : elements) {
            copy.add(el.deepCopy());
        }
        return copy;
    }

    public void set(int index, JsonElement element) {
        elements.set(index, element);
    }

    public void remove(int index) {
        elements.remove(index);
    }


    @Override
    public String toString() {
        return elements.toString();
    }
}