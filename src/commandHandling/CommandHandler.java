package src.commandHandling;

import src.json.commands.*;
import src.json.JsonFileHandler;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    private final Map<CommandIdentifier, Command> commandHandling = new HashMap<CommandIdentifier, Command>();
    private JsonFileHandler jsonFileHandler;
    public CommandHandler()
    {
        jsonFileHandler = new JsonFileHandler();

        commandHandling.put(CommandIdentifier.OPEN, new Open(jsonFileHandler));
        commandHandling.put(CommandIdentifier.VALIDATE, new Validate(jsonFileHandler));
        commandHandling.put(CommandIdentifier.PRINT, new Print(jsonFileHandler));
        commandHandling.put(CommandIdentifier.SET, new Set(jsonFileHandler));
        commandHandling.put(CommandIdentifier.SEARCH, new Search(jsonFileHandler));
        commandHandling.put(CommandIdentifier.HELP, new Help());
        commandHandling.put(CommandIdentifier.SAVE, new Save(jsonFileHandler));
        commandHandling.put(CommandIdentifier.SAVEAS, new SaveAs(jsonFileHandler));
        commandHandling.put(CommandIdentifier.REMOVE, new Remove(jsonFileHandler));
        commandHandling.put(CommandIdentifier.MOVE, new Move(jsonFileHandler));
        commandHandling.put(CommandIdentifier.CREATE, new Create(jsonFileHandler));

    }

    public String handleCommand(String command, String[] args)
    {

        String commandResult;
        CommandIdentifier identifier;
        try {
           identifier = CommandIdentifier.fromString(command);
        }catch (IllegalArgumentException ex)
        {
            return ex.getMessage();
        }

        Command executableCommand = commandHandling.get(identifier);
        if(executableCommand != null)
          commandResult = executableCommand.execute(args);
        else
            commandResult = "No such command!";


        return commandResult;
    }



}
