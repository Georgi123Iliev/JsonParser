package src.json.commands;

import src.json.JsonFileHandler;

public class Remove implements Command {

    private JsonFileHandler jsonFileHandler;;

    public Remove(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }

    @Override
    public String execute(String[] args)
    {
        if(args.length != 1) return "Incorrect argument count";
        return jsonFileHandler.remove(args[0]);
    }
}
