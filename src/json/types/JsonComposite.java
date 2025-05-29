package src.json.types;

import src.exception.NotFoundException;

import java.util.Queue;

public abstract class JsonComposite implements JsonElement {
    @Override
    public String extraNewline() {
        return "";
    }




}
