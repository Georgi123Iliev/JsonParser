package src.json.commands;

import src.json.JsonFileHandler;

public class Search implements ICommand{

    @Override
    public String Execute(JsonFileHandler jsonFileHandler, String[] args) {

        if(args.length!=1)
        {
            return "Incorrect argument count";
        }



        return jsonFileHandler.search(args[0]);
    }
}
