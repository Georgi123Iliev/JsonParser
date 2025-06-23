package src.commandHandling;

import src.json.FileHandler;
import src.json.commands.*;
import src.json.JsonFileHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Central dispatcher that converts a textual command into a concrete {@link Command} implementation
 * and executes it.
 * <p>
 * A single {@link JsonFileHandler} instance is shared between commands that require
 * file access. The mapping between {@link CommandIdentifier identifiers} and command objects
 * is populated once in the constructor and remains immutable afterwards.
 */
public class CommandHandler {

    /** Lazily‑constructed registry of commands keyed by their identifier. */
    private final Map<CommandIdentifier, Command> commandHandling = new HashMap<>();

    private JsonFileHandler jsonFileHandler;
    private FileHandler fileHandler;

    /**
     * Creates all command objects and registers them in the internal map.
     * Each command that manipulates JSON receives the same {@link JsonFileHandler}
     * so they operate on shared state.
     */
    public CommandHandler()
    {
        jsonFileHandler = new JsonFileHandler();
        fileHandler = new FileHandler();

        commandHandling.put(CommandIdentifier.OPEN, new Open(fileHandler));
        commandHandling.put(CommandIdentifier.VALIDATE, new Validate(fileHandler,jsonFileHandler));
        commandHandling.put(CommandIdentifier.PRINT, new Print(jsonFileHandler));
        commandHandling.put(CommandIdentifier.SET, new Set(jsonFileHandler));
        commandHandling.put(CommandIdentifier.SEARCH, new Search(jsonFileHandler));
        commandHandling.put(CommandIdentifier.HELP, new Help());
        commandHandling.put(CommandIdentifier.SAVE, new Save(fileHandler, jsonFileHandler));
        commandHandling.put(CommandIdentifier.SAVEAS, new SaveAs(fileHandler,jsonFileHandler));
        commandHandling.put(CommandIdentifier.REMOVE, new Remove(jsonFileHandler));
        commandHandling.put(CommandIdentifier.MOVE, new Move(jsonFileHandler));
        commandHandling.put(CommandIdentifier.CREATE, new Create(jsonFileHandler));
        commandHandling.put(CommandIdentifier.CLOSE, new Close(fileHandler,jsonFileHandler));


    }

    /**
     * Executes a command given its textual name and argument list.
     *
     * @param command raw command as typed by the user (e.g. {@code "open"})
     * @param args    arguments forwarded to the command's {@code execute} method
     * @return the command's output or an explanatory message if the command is unknown
     */
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
