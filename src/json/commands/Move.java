package src.json.commands;

import src.json.JsonFileHandler;

public class Move implements Command {

    private JsonFileHandler jsonFileHandler;

    public Move(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }

    /**
     * Moves a JSON element from one path to another within
     * the document.
     */

    /**
     * Moves an element from the first path to the second.
     *
     * @param args source and destination path arguments
     * @return result message from the operation
     */
    @Override
    public String execute(String[] args) {
        if(args.length != 2)
            return "Wrong argument count";


            return jsonFileHandler.move(args[0], args[1]);

    }
}
