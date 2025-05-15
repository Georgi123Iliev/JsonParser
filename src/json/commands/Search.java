package src.json.commands;

import src.json.JsonFileHandler;

public class Search implements Command {


    private JsonFileHandler jsonFileHandler;

    public Search(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }

    @Override
    public String execute(String[] args) {

        if(args.length!=1)
        {
            return "Incorrect argument count";
        }



        return jsonFileHandler.search(args[0]);
    }
}
