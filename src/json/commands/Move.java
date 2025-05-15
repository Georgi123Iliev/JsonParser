package src.json.commands;

import src.json.JsonFileHandler;

public class Move implements Command {

    private JsonFileHandler jsonFileHandler;

    public Move(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }

    @Override
    public String execute(String[] args) {
        if(args.length != 2)
            return "Wrong argument count";


            return jsonFileHandler.move(args[0], args[1]);

    }
}
