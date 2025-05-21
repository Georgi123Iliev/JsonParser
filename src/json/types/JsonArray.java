package src.json.types;

import java.util.*;

/**
 * Represents a JSON array, holding an ordered list of {@code JsonElement}s.
 */
public class JsonArray implements JsonElement {
    private final List<JsonElement> elements;

    /**
     * Constructs a {@code JsonArray} from an existing list.
     *
     * @param elements the initial list of elements
     */
    public JsonArray(List<JsonElement> elements) {
        this.elements = elements;
    }

    /**
     * Constructs an empty {@code JsonArray}.
     */
    public JsonArray() {
        elements = new ArrayList<>();
    }

    /**
     * Adds an element to the array.
     *
     * @param element the element to add
     */
    public void add(JsonElement element) {
        elements.add(element);
    }

    /**
     * Retrieves the element at a given index.
     *
     * @param index the index to access
     * @return the {@code JsonElement} at that index
     */
    public JsonElement get(int index) {
        return elements.get(index);
    }

    /**
     * @return true if the array is empty
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Adds all elements from another {@code JsonArray}.
     *
     * @param other the array to append from
     */
    public void addAll(JsonArray other) {
        this.elements.addAll(other.elements);
    }

    /**
     * @return the number of elements in the array
     */
    public int size() {
        return elements.size();
    }

    /**
     * Creates a deep copy of the array.
     *
     * @return a new {@code JsonArray} with copied elements
     */
    @Override
    public JsonElement deepCopy() {
        JsonArray copy = new JsonArray();
        for (JsonElement el : elements) {
            copy.add(el.deepCopy());
        }
        return copy;
    }

    /**
     * Replaces the element at the specified index.
     *
     * @param index the position to modify
     * @param element the new element to set
     */
    public void set(int index, JsonElement element) {
        elements.set(index, element);
    }

    /**
     * Removes the element at the specified index.
     *
     * @param index the position to remove
     */
    public void remove(int index) {
        elements.remove(index);
    }

    /**
     * @return string representation of the array
     */
    @Override
    public String toString() {
        return elements.toString();
    }
}