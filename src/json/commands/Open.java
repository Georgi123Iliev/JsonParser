package src.json.commands;

import src.json.JsonFileHandler;
/**
 * Opens a JSON file and loads it into memory.
 */


public class Open implements Command {

    private JsonFileHandler jsonFileHandler;

    public Open(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }



    /**
     * Opens the specified file.
     *
     * @param args expects exactly one path argument
     * @return outcome message from the operation
     */

    @Override
    public String execute(String[] args) {

        if(args.length!=1)
            return "Incorrect argument count";

        return jsonFileHandler.openFile(args[0]);


    }
}
