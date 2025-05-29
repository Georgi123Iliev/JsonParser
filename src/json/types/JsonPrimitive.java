package src.json.types;

import src.exception.InvalidJsonPathException;
import src.exception.NotFoundException;

import java.util.Queue;

public abstract class JsonPrimitive implements JsonElement {
    @Override
    public String extraNewline() {
        return "\n";
    }

    @Override
    public void completePath(Queue<String> path) throws InvalidJsonPathException {
        if(path.isEmpty())
            return;
        throw new InvalidJsonPathException("Invalid path");
    }

    @Override
    public JsonArray search(String key) {return new JsonArray();}

    @Override
    public JsonElement getValueAt(Queue<String> jsonPath, String previousKey) throws NotFoundException {
        if(jsonPath.isEmpty())
            return this;

        throw new NotFoundException("Invalid JSON structure at '" + previousKey + "', cannot descend into primitive");
    }

    @Override
    public String assign(Queue<String> jsonPath, String previousKey, JsonElement valueToAdd) throws NotFoundException {
        return "Invalid JSON structure: Cannot descend into primitive at " + previousKey;
    }


}
