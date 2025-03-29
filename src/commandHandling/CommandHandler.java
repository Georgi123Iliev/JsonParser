package src.commandHandling;

import src.json.commands.*;
import src.json.JsonFileHandler;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    private final Map<String, ICommand> commandHandling = new HashMap<String,ICommand>();
    private JsonFileHandler jsonFileHandler;
    public CommandHandler()
    {
        jsonFileHandler = new JsonFileHandler();

        commandHandling.put("open",new Open());
        commandHandling.put("validate",new Validate());
        commandHandling.put("print",new Print());
        commandHandling.put("set",new Set());
        commandHandling.put("search", new Search());
        commandHandling.put("help",new Help());

    }

    public String HandleCommand(String command, String[] args)
    {

        String commandResult;

            var executableCommand = commandHandling.get(command);
            if(executableCommand != null)
              commandResult = executableCommand.Execute(jsonFileHandler,args);
            else
                commandResult = "No such command!";


        return commandResult;
    }



}
