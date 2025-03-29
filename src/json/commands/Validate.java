package src.json.commands;

import src.json.JsonFileHandler;

public class Validate implements ICommand {

    @Override
    public String Execute(JsonFileHandler jsonFileHandler, String[] args) {

        if(args.length != 0)
            return "Incorrect argument count";

       return jsonFileHandler.Validate();
    }
}
