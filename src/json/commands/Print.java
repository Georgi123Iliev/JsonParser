package src.json.commands;

import src.json.JsonFileHandler;

/**
 * Prints the currently-loaded JSON document in structured form.
 */
public class Print implements Command {

    private JsonFileHandler jsonFileHandler;

    public Print(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }


    /**
     * Prints the formatted JSON.
     *
     * @param args expects no arguments
     * @return the pretty-printed JSON or an error message
     */

    @Override
    public String execute(String[] args) {

        if(args.length != 0)
        {
            return "Incorrect argument count";
        }

        return jsonFileHandler.getStructuredJson();
    }
}
