package src;

import src.commandHandling.CommandHandler;

import java.util.Arrays;
import java.util.Scanner;

public class CLI {
    private CommandHandler commandHandler;

    public void run()
    {
        commandHandler = new CommandHandler();

        while(true) {
            System.out.print("> ");
            Scanner scanner = new Scanner(System.in);

            String line = scanner.nextLine().trim();

            if(line.compareToIgnoreCase("exit") == 0)
                break;

            var items = line.split(" ");


            if(items.length == 0) continue;

            String command = items[0];

            String[] arguments = Arrays.copyOfRange(items,1,items.length);

            String result = commandHandler.HandleCommand(command,arguments);

            System.out.println(result);

        }
    }

}
