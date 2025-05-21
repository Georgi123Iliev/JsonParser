package src.json.commands;

import src.json.JsonFileHandler;

/**
 * Removes a JSON element at the specified path.
 */
public class Remove implements Command {

    private JsonFileHandler jsonFileHandler;;

    public Remove(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }
    /**
     * Deletes an element at the given path.
     *
     * @param args expects one path argument
     * @return result message from the deletion
     */
    @Override
    public String execute(String[] args)
    {
        if(args.length != 1) return "Incorrect argument count";
        return jsonFileHandler.remove(args[0]);
    }
}
