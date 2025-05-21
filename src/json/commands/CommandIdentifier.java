package src.json.commands;

/**
 * Enumeration of all supported command keywords accepted by the CLI.
 * Each constant stores its lowercase textual representation so the user is
 * free to type commands in any mix of cases.
 */
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
    CREATE("create"),
    CLOSE("close");
    /** Literal keyword entered by the user (always lowercase). */
    private final String value;

    /**
     * Converts a raw command string to its corresponding identifier.
     * The comparison is case‑insensitive.
     *
     * @param input user‑supplied command token
     * @return matching {@link CommandIdentifier}
     * @throws IllegalArgumentException if {@code input} does not match any command
     */
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

    /**
     * @return the lowercase keyword for this command
     */
    public String getValue() {
        return value;
    }
}
