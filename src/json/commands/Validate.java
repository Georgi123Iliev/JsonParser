package src.json.commands;

import src.json.FileHandler;
import src.json.JsonFileHandler;
/**
 * Validates the syntax of the currently-opened JSON file.
 * Reports “Json is valid” or returns the stored parse error.
 */



public class Validate implements Command {

   private JsonFileHandler jsonFileHandler;
   private FileHandler fileHandler;

    public Validate(FileHandler fileHandler, JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
        this.fileHandler = fileHandler;
    }

    /**
     * Executes the <code>validate</code> command.
     *
     * @param args expects no arguments
     * @return validation result (“Json is valid” or an error message)
     */
    @Override
    public String execute(String[] args) {

        if(args.length != 0)
            return "Incorrect argument count";





       return jsonFileHandler.validate(fileHandler.getRawText());
    }
}
