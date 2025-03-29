package src.json.commands;

import src.json.JsonFileHandler;

public class Open implements ICommand {

    @Override
    public String Execute(JsonFileHandler jsonObject, String[] args) {

        if(args.length!=1)
            return "Incorrect argument count";

        jsonObject.OpenFile(args[0]);

        return "";
    }
}
