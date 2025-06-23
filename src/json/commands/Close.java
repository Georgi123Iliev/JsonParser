package src.json.commands;

import src.json.FileHandler;
import src.json.JsonFileHandler;
/**
 * Closes the currently-opened JSON file, discarding unsaved
 * changes if they have already been saved.
 */



public class Close implements Command {

    private JsonFileHandler jsonFileHandler;
    private FileHandler fileHandler;

    public Close(FileHandler fileHandler,JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
        this.fileHandler = fileHandler;
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
        fileHandler.close();
        return jsonFileHandler.close();
    }
}
