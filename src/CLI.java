package src;

import src.commandHandling.CommandHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * A minimal interactive command‑line interface.
 * <p>
 * The {@code CLI} continuously reads lines from {@code System.in}, tokenizes them with
 * shell‑like rules (quoted segments become single tokens), delegates execution to a
 * {@link CommandHandler}, and prints the handler's response.
 * Typing {@code exit} (case‑insensitive) terminates the loop.
 */
public class CLI {
    /**
     * Handles execution of parsed commands.
     */
    private CommandHandler commandHandler;

    /**
     * Tokenizes a raw command line using simple shell‑style semantics.
     * Whitespace separates tokens unless the whitespace appears inside double quotes.
     *
     * @param input raw user input
     * @return array where element&nbsp;0 is the command and the rest are its arguments
     * @throws IllegalArgumentException if a quoted string is not properly closed
     */
    private String[] parseCommand(String input) throws IllegalArgumentException {
        List<String> args = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);


            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (Character.isWhitespace(c)) {
                if (inQuotes) {
                    current.append(c);
                } else {
                    if (current.length() > 0) {
                        args.add(current.toString());
                        current.setLength(0);
                    }
                }
            } else {
                current.append(c);
            }
        }




        if (inQuotes) {
            throw new IllegalArgumentException("Invalid input: Unterminated quoted string.");
        }

        if (current.length() > 0) {
            args.add(current.toString().replace('\'','\"'));
        }

        return args.toArray(new String[0]);
    }

    /**
     * Launches the read–eval–print loop.
     * Prompts the user with {@code "> "}, parses each line, and invokes
     * {@link CommandHandler#handleCommand(String, String[])} with the extracted
     * command and arguments. The loop exits when the user types {@code exit}.
     */
    public void run()
    {
        commandHandler = new CommandHandler();

        while(true) {
            System.out.print("> ");
            Scanner scanner = new Scanner(System.in);

            String line = scanner.nextLine().trim();

            if(line.compareToIgnoreCase("exit") == 0)
                break;

            String[] items;
            try {


                items = parseCommand(line);
            }catch (IllegalArgumentException ex)
            {
                System.out.println(ex.getMessage());
                continue;
            }


            if(items.length == 0) continue;

            String command = items[0];

            String[] arguments = Arrays.copyOfRange(items,1,items.length);

            String result = commandHandler.handleCommand(command,arguments);

            System.out.println(result);

        }
    }

}
