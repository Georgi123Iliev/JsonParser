package src.json.commands;

import src.json.JsonFileHandler;

public class Save implements ICommand{


    @Override
    public String Execute(JsonFileHandler jsonFileHandler, String[] args) {
        if(args.length != 0)
        {
            return "Грешен брой аргументи";
        }

        return jsonFileHandler.save();
    }
}
