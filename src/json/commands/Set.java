package src.json.commands;

import src.json.JsonFileHandler;

public class Set implements Command {

    private JsonFileHandler jsonFileHandler;


    public Set(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }

    @Override
    public String execute(String[] args) {

        if(args.length != 2)
            return "Incorrect argument count";

    return jsonFileHandler.set(args[0],args[1]);


    }
}
