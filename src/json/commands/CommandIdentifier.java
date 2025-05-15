package src.json.commands;

public enum CommandIdentifier {
    OPEN("open"),
    VALIDATE("validate"),
    PRINT("print"),
    SET("set"),
    SEARCH("search"),
    HELP("help"),
    SAVE("save"),
    SAVEAS("saveas"),
    REMOVE("remove"),
    MOVE("move"),
    CREATE("create");

    private final String value;

    // Method to get enum from string
    public static CommandIdentifier fromString(String input) {
        for (CommandIdentifier command : CommandIdentifier.values()) {
            if (command.getValue().equalsIgnoreCase(input)) {
                return command;
            }
        }
        throw new IllegalArgumentException("Invalid command: " + input);
    }

    CommandIdentifier(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
