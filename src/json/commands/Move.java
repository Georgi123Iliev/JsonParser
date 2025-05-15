package src.json.commands;

import src.json.JsonFileHandler;

public class Move implements ICommand{

    private JsonFileHandler jsonFileHandler;

    @Override
    public String Execute(JsonFileHandler jsonFileHandler, String[] args) {
        if(args.length != 2)
            return "Wrong argument count";
        return jsonFileHandler.move(args[0],args[1]);
    }
}
