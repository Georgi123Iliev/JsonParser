package src.json.commands;

import src.json.JsonFileHandler;
/**
 * Replaces or inserts a value at a specific JSON path within the document.
 * Equivalent CLI usage: <code>set &lt;path&gt; &lt;jsonValue&gt;</code>.
 */



public class Set implements Command {

    private JsonFileHandler jsonFileHandler;


    public Set(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }

    /**
     * Executes the <code>set</code> command.
     *
     * @param args {@code args[0]} is the destination path,
     *             {@code args[1]} is the JSON literal or fragment to store
     * @return result message from the operation
     */
    @Override
    public String execute(String[] args) {

        if(args.length != 2)
            return "Incorrect argument count";

    return jsonFileHandler.set(args[0],args[1]);


    }
}
