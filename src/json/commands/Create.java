package src.json.commands;

import src.json.JsonFileHandler;

public class Create implements ICommand{

    @Override
    public String Execute(JsonFileHandler jsonFileHandler, String[] args) {
        return jsonFileHandler.create(args[0],args[1]);
    }
}
