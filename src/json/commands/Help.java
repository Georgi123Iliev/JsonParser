package src.json.commands;

public class Help implements Command {

    private String helpMessage = "Available Commands:\n"
            + "validate: Checks if the opened file is valid JSON syntax. Reports any issues with details.\n"
            + "print: Displays the contents of the object in a readable format.\n"
            + "search <key>: Searches for data under the specified key and lists the found values.\n"
            + "set <path> <string>: Sets the value at the specified path with the provided JSON string, if the path exists.\n"
            + "create <path> <string>: Creates an element at the specified path with the given JSON string.\n"
            + "delete <path>: Deletes the element at the specified path if it exists.\n"
            + "move <from> <to>: Moves an element from the specified path to another path.\n"
            + "save [<path>]: Saves the object at the specified path or the whole object if no path is provided.\n"
            + "saveas <file> [<path>]: Saves the object to a new file. If a path is provided, saves that part of the object.\n";



    @Override
    public String execute(String[] args) {
        return helpMessage;
    }


}
