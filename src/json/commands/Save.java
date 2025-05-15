package src.json.commands;

import src.json.JsonFileHandler;

public class Save implements Command {

    private JsonFileHandler jsonFileHandler;

    public Save(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }

    @Override
    public String execute(String[] args) {
        if(args.length != 0)
        {
            return "Incorrect argument count";
        }

        return jsonFileHandler.save();
    }
}
