package src;

import src.commandHandling.CommandHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CLI {
    private CommandHandler commandHandler;

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
            args.add(current.toString());
        }

        return args.toArray(new String[0]);
    }

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
