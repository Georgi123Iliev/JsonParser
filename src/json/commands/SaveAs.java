package src.json.commands;

import src.json.JsonFileHandler;

public class SaveAs implements Command {

    private JsonFileHandler jsonFileHandler;

    public SaveAs(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }

    @Override
    public String execute(String[] args)
    {
        if(args.length == 1) {
            return jsonFileHandler.saveAs(args[0]);
        }else if(args.length == 2) return jsonFileHandler.saveAs(args[0],args[1]);

        return "Incorrect argument count";
    }
}
