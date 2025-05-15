package src.json.commands;

import src.json.JsonFileHandler;

public class Create implements Command {

    JsonFileHandler jsonFileHandler;

    public Create(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }

    @Override
    public String execute(String[] args) {
        return jsonFileHandler.create(args[0],args[1]);
    }
}
