package src.json.commands;

import src.json.JsonFileHandler;
/**
 * Creates a JSON element at the specified path, adding
 * intermediary nodes as needed.
 */
public class Create implements Command {

   private JsonFileHandler jsonFileHandler;

    public Create(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }
    /**
     * Creates an element at the path with the provided JSON literal.
     *
     * @param args path and JSON value arguments
     * @return message indicating the outcome
     */
    @Override
    public String execute(String[] args) {
        return jsonFileHandler.create(args[0],args[1]);
    }
}
