package src.json.commands;

import src.json.JsonFileHandler;

public class Set implements ICommand{


    @Override
    public String Execute(JsonFileHandler jsonFileHandler, String[] args) {

        if(args.length != 2)
            return "Incorrect argument count";



        return "";
    }
}
