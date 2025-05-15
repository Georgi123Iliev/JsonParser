package src.commandHandling;

import src.json.commands.*;
import src.json.JsonFileHandler;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    private final Map<CommandIdentifier, ICommand> commandHandling = new HashMap<CommandIdentifier,ICommand>();
    private JsonFileHandler jsonFileHandler;
    public CommandHandler()
    {
        jsonFileHandler = new JsonFileHandler();

        commandHandling.put(CommandIdentifier.OPEN, new Open());
        commandHandling.put(CommandIdentifier.VALIDATE, new Validate());
        commandHandling.put(CommandIdentifier.PRINT, new Print());
        commandHandling.put(CommandIdentifier.SET, new Set());
        commandHandling.put(CommandIdentifier.SEARCH, new Search());
        commandHandling.put(CommandIdentifier.HELP, new Help());
        commandHandling.put(CommandIdentifier.SAVE, new Save());
        commandHandling.put(CommandIdentifier.SAVEAS, new SaveAs());
        commandHandling.put(CommandIdentifier.REMOVE, new Remove());
        commandHandling.put(CommandIdentifier.MOVE, new Move());
        commandHandling.put(CommandIdentifier.CREATE, new Create());

    }

    public String HandleCommand(String command, String[] args)
    {

        String commandResult;
        CommandIdentifier identifier;
        try {
           identifier = CommandIdentifier.fromString(command);
        }catch (IllegalArgumentException ex)
        {
            return ex.getMessage();
        }

        ICommand executableCommand = commandHandling.get(identifier);
        if(executableCommand != null)
          commandResult = executableCommand.Execute(jsonFileHandler,args);
        else
            commandResult = "No such command!";


        return commandResult;
    }



}
