package src.json.commands;

import src.json.JsonFileHandler;

public class Print implements Command {

    private JsonFileHandler jsonFileHandler;

    public Print(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }

    @Override
    public String execute(String[] args) {

        if(args.length != 0)
        {
            return "Incorrect argument count";
        }

        return jsonFileHandler.getStructuredJson();
    }
}
