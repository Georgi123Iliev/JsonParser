package src.json.commands;

import src.json.JsonFileHandler;

public class Close implements Command {

    JsonFileHandler jsonFileHandler;

    public Close(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }

    @Override
    public String execute(String[] args) {
        if(args.length != 0)
            return "Incorrect argument count";
        return jsonFileHandler.close();
    }
}
