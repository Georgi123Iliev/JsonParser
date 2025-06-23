package src.json;

import src.json.commands.SaveAs;
import src.json.types.JsonElement;
import src.json.types.JsonObject;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler {

    private Path filePath;
    private JsonElement jsonObject;
    private String rawText;

    /**
     * Opens a JSON file, parses it and stores the result in memory.
     *
     * @param fileName path to the file on disk
     * @return empty string on success, otherwise an explanatory message
     */
    public String openFile(String fileName) {



        Path path = Path.of(fileName);

        if (!Files.exists(path)) {
            saveAs(fileName,new JsonObject().toJson(0));
        }

        try {
            rawText = Files.readString(path);
            filePath = path;

        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }

        return "File opened";
    }

    /**
     * Saves {@code jsonObject} to {@code fileName}.
     *
     * @param fileName destination path
     * @param jsonString subtree(string) to write
     * @return empty string on success or error message
     */
    public String saveAs(String fileName, String jsonString) {
        Path path = Path.of(fileName);
        try {
            try {
                Files.createFile(path);
            } catch (FileAlreadyExistsException ignored) {
            }
            Files.writeString(path,jsonString);
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }

        return "File saved";
    }


    public String saveAs(String jsonString) {

        try {
            try {
                Files.createFile(filePath);
            } catch (FileAlreadyExistsException ignored) {
            }
            Files.writeString(filePath,jsonString);
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }

        return "File saved";
    }


    /**
     * Saves the document to the originally opened file.
     *
     * @return empty string on success or error message
     */
    public String save() {
        return filePath != null ? saveAs(filePath.toString()) : "No file open";
    }

    public String save(String rawText) {
        return filePath != null ? saveAs(filePath.toString(),rawText) : "No file open";
    }

    public String getRawText() { return rawText; }

    public String close()
    {
        if(filePath != null)
            return "File already closed";
        filePath = null;
        rawText = null;

        return "File closed";
    }
}


