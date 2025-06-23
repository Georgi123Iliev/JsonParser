package src.json.commands;

import src.json.FileHandler;
import src.json.JsonFileHandler;
/**
 * Saves the current JSON document—or a subtree of it—to a new file.
 * Behaves like the shell command <code>saveas &lt;file&gt; [&lt;path&gt;]</code>.
 */


public class SaveAs implements Command {

    private JsonFileHandler jsonFileHandler;
    private FileHandler fileHandler;

    public SaveAs(FileHandler fileHandler, JsonFileHandler jsonFileHandler) {
        this.fileHandler = fileHandler;
        this.jsonFileHandler = jsonFileHandler;
    }
    /**
     * Executes the <code>saveas</code> command.
     *
     * @param args if length == 1, {@code args[0]} is the target file;
     *             if length == 2, {@code args[0]} is the target file and
     *             {@code args[1]} is a JSON path whose subtree will be saved
     * @return empty string on success or an explanatory message
     */

    @Override
    public String execute(String[] args)
    {
        if(args.length == 1) {
            return fileHandler.saveAs(jsonFileHandler.getStructuredJson());
        }else if(args.length == 2) return fileHandler.saveAs(args[0],args[1]);




        return "Incorrect argument count";
    }
}
