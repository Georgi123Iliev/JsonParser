package src.json.commands;

import src.json.FileHandler;
import src.json.JsonFileHandler;
/**
 * Opens a JSON file and loads it into memory.
 */


public class Open implements Command {

    private FileHandler fileHandler;

    public Open(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
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


        return fileHandler.openFile(args[0]);


    }
}
