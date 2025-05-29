package src.json.types;

import src.exception.InvalidJsonPathException;
import src.exception.NotFoundException;

import java.util.*;

/**
 * Represents a JSON array, holding an ordered list of {@code JsonElement}s.
 */
public class JsonArray extends JsonComposite {
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

    public String toJson(int indent) {
        StringBuilder sb = new StringBuilder();
        String padding = "\t".repeat(Math.max(0, indent));
        String biggerPadding =  "\t".repeat(indent+1);

        sb.append("\n").append(padding).append("[");

        int i=0;
        for (JsonElement el : elements) {


            sb.append(el.extraNewline());
            sb.append(biggerPadding);
            sb.append(el.toJson(indent+1));
            if(i != elements.size()-1) sb.append(",");


            i++;
        }
        sb.append("\n").append(padding).append("]");

        return sb.toString();

    }

    @Override
    public JsonArray search(String key) {

        JsonArray result = new JsonArray();

        for (JsonElement el : elements) {
            result.addAll(el.search(key));
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

        if (!head.matches("\\[\\d+\\]")) {
            throw new InvalidJsonPathException("Expected array index but got object key '" + head + "'");
        }

        int index = Integer.parseInt(head.substring(1, head.length() - 1));
        if (index == size()) {
            add(nextElement);
        } else if (index > size()) {
            throw new InvalidJsonPathException("Cannot complete path: trying to add index " + index + " before completing previous indices.");
        }


        get(index).completePath(jsonPath);
    }

    @Override
    public JsonElement getValueAt(Queue<String> jsonPath, String previousKey) throws NotFoundException {
        String head = jsonPath.poll();

        if(head == null)
            return this;

        if (!head.matches("\\[\\d+\\]")) {
            throw new NotFoundException("In array " + previousKey + ", '" + head + "' is not a valid index");
        }

        int index = Integer.parseInt(head.substring(1, head.length() - 1));
        if (index >= size()) {
            throw new NotFoundException(head + " is greater than the number of items in array " + previousKey);
        }

        JsonElement element = get(index);
        return element.getValueAt(jsonPath, head);

        }

    @Override
    public String assign(Queue<String> jsonPath, String previousKey, JsonElement valueToAdd) throws NotFoundException {


        if (jsonPath.isEmpty()) {
            return "Empty JSON path";
        }

        String head = jsonPath.poll();

        if (!head.matches("\\[\\d+\\]")) {
            return "In array " + previousKey + ", '" + head + "' is not a valid index format";
        }

        int index = Integer.parseInt(head.substring(1, head.length() - 1));
        if (index >= size()) {
            return head + " is greater than the number of items in the array " + previousKey;
        }

        if (jsonPath.isEmpty()) {
            if (valueToAdd instanceof Nothing) {
                remove(index);
                return "Element removed";
            } else {
                set(index, valueToAdd);
                return "Element set";
            }
        }

        return get(index).assign(jsonPath, head, valueToAdd);
    }
}