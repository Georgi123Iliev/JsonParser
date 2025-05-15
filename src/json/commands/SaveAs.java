package src.json.commands;

import src.json.JsonFileHandler;

public class SaveAs implements ICommand{
    @Override
    public String Execute(JsonFileHandler jsonFileHandler, String[] args) {
         return jsonFileHandler.saveAs(args[0]);
    }
}
