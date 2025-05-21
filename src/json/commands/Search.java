package src.json.commands;

import src.json.JsonFileHandler;
/**
 * Searches the loaded JSON document for every occurrence of a given key
 * and prints the values found.
 */



public class Search implements Command {


    private JsonFileHandler jsonFileHandler;

    public Search(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }

    /**
     * Executes the <code>search</code> command.
     *
     * @param args expects exactly one argument—the key to search for
     * @return formatted list of values, or a not-found / error message
     */
    @Override
    public String execute(String[] args) {

        if(args.length!=1)
        {
            return "Incorrect argument count";
        }



        return jsonFileHandler.search(args[0]);
    }
}
