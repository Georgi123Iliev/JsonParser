package src.json.commands;

/**
 * Command interface in the Command pattern: each concrete CLI
 * command implements this.
 */



public interface Command {

    /**
     * Executes the command.
     *
     * @param args arguments passed from the CLI
     * @return result string to display to the user
     */

    String execute(String[] args);


}
