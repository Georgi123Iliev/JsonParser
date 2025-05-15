package src.json.commands;

import src.json.JsonFileHandler;

public class Remove implements ICommand{

    @Override
    public String Execute(JsonFileHandler jsonFileHandler, String[] args)
    {
        if(args.length != 1) return "Wrong argument count";
        return jsonFileHandler.remove(args[0]);
    }
}
