package src.json.commands;

import src.json.JsonFileHandler;
/**
 * Closes the currently-opened JSON file, discarding unsaved
 * changes if they have already been saved.
 */



public class Close implements Command {

    JsonFileHandler jsonFileHandler;

    public Close(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }

    /**
     * Closes the current file.
     *
     * @param args expects no arguments
     * @return confirmation or error message
     */
    @Override
    public String execute(String[] args) {
        if(args.length != 0)
            return "Incorrect argument count";
        return jsonFileHandler.close();
    }
}
