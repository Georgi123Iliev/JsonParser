package src.json.commands;

import src.json.FileHandler;
import src.json.JsonFileHandler;
/**
 * Saves the currently-open JSON document back to its original file.
 */
public class Save implements Command {

    private JsonFileHandler jsonFileHandler;
    private FileHandler fileHandler;
    public Save(FileHandler fileHandler, JsonFileHandler jsonFileHandler) {
        this.fileHandler = fileHandler;
        this.jsonFileHandler = jsonFileHandler;
    }
    /**
     * Saves the document.
     *
     * @param args expects no arguments
     * @return empty string on success or an error message
     */
    @Override
    public String execute(String[] args) {
        if(args.length != 0)
        {
            return "Incorrect argument count";
        }

        return fileHandler.save(jsonFileHandler.getStructuredJson());
    }
}
