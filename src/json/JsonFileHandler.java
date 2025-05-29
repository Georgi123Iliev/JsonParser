package src.json;

import src.exception.InvalidJsonPathException;
import src.exception.NotFoundException;
import src.json.Parsing.JsonParseResult;
import src.json.Parsing.JsonParser;
import src.json.Parsing.ValueParseResult;
import src.json.Parsing.ValueParser;
import src.json.types.JsonArray;
import src.json.types.JsonElement;
import src.json.types.JsonObject;
import src.json.types.Nothing;
import src.validators.JsonPathIntersectionValidator;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Manages a single JSON document in memory and provides file I/O plus path-based editing utilities.
 */
public class JsonFileHandler {

    private Path filePath;
    private JsonElement jsonObject;
    private String rawText;


    /** Creates an empty handler with no file loaded. */
    public JsonFileHandler() {}

    /**
     * Opens a JSON file, parses it and stores the result in memory.
     *
     * @param fileName path to the file on disk
     * @return empty string on success, otherwise an explanatory message
     */
    public String openFile(String fileName) {


        jsonObject = null;
        Path path = Path.of(fileName);

        if (!Files.exists(path)) {
           saveAs(fileName,new JsonObject());
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
     * Deletes the element located at {@code jsonPath}.
     *
     * @param jsonPath dot / bracket path, e.g. {@code root.array[0]}
     * @return result message
     */
    public String remove(String jsonPath) {
        if(jsonObject == null)
            return "No file opened";

        Queue<String> pathQueue = parseJsonPath(jsonPath);
        try {
            return assign(pathQueue, jsonObject, "Main object", Nothing.INSTANCE);
        } catch (NotFoundException ex) {
            return ex.getMessage();
        }
    }

    /**
     * Core recursive routine that traverses the structure and either sets or removes a value.
     *
     * @param jsonPath    queue of path tokens (mutated during traversal)
     * @param currentData element currently inspected
     * @param previousKey last object key or array index (for error context)
     * @param valueToAdd  value to insert; {@link Nothing#INSTANCE} signals removal
     * @return message indicating the performed action
     * @throws NotFoundException if the path cannot be resolved
     */
    private String assign(Queue<String> jsonPath,
                          JsonElement currentData,
                          String previousKey,
                          JsonElement valueToAdd) throws NotFoundException {
        if(currentData == null)
            return "No file opened";

        return currentData.assign(jsonPath, previousKey, valueToAdd);

    }

    /**
     * Moves data from {@code from} to {@code to} after validating that the paths do not overlap.
     *
     * @param from source path
     * @param to   destination path
     * @return result message
     */
    public String move(String from, String to) {
        if(jsonObject == null)
            return "No file opened";
        var validationResult = JsonPathIntersectionValidator.validate(from, to);
        if (!validationResult.isSuccess()) {
            return validationResult.getReasonForFailure();
        }

        Queue<String> fromQueue = parseJsonPath(from);
        JsonElement value;
        var copiedObj = jsonObject.deepCopy();
        try {

            value = copiedObj.getValueAt(fromQueue, "Main object");
            assign(fromQueue, copiedObj, "Main object", Nothing.INSTANCE);

            String result = assign(parseJsonPath(to), copiedObj, "Main object", value);

            jsonObject = copiedObj;
            return result;
        } catch (NotFoundException ex) {

            return ex.getMessage();
        }
    }

    /**
     * Splits a dotted/bracket JSON path into individual tokens.
     *
     * @param path raw path expression
     * @return queue of tokens ready for traversal
     */
    private Queue<String> parseJsonPath(String path) {
        Queue<String> pathQueue = new ArrayDeque<>();
        for (String segment : path.split("\\.")) {
            int bracketIndex;
            String substr = segment;
            boolean wasSplit = false;
            while (true) {
                bracketIndex = substr.indexOf('[');
                if (bracketIndex != -1) {
                    if (bracketIndex != 0) {
                        pathQueue.add(substr.substring(0, bracketIndex));
                    }
                    int closingIndex = substr.indexOf(']');
                    pathQueue.add(substr.substring(bracketIndex, closingIndex + 1));
                    substr = substr.substring(closingIndex + 1);
                    wasSplit = true;
                } else {
                    break;
                }
            }
            if (!wasSplit) {
                pathQueue.add(segment);
            }
        }
        return pathQueue;
    }

    /**
     * Inserts or replaces a value at the given path.
     *
     * @param jsonPath  destination path
     * @param jsonValue literal or JSON text representing the new value
     * @return result message
     */
    public String set(String jsonPath, String jsonValue) {

        if(jsonObject == null)
            return "No file opened";

        ValueParseResult valueParseResult = ValueParser.parseValue(jsonValue);
        JsonParseResult parseResult = JsonParser.parseJson(jsonValue);
        JsonElement toAssign;
        if (!valueParseResult.isSuccess&&!parseResult.isSuccess()) {
            return "Bad json string";
        }
        else if(valueParseResult.isSuccess)
        {
            toAssign = valueParseResult.parsedValue;
        }
        else{

            toAssign = parseResult.parsedData;;
        }

        try {
            return assign(parseJsonPath(jsonPath), jsonObject, "Main object", toAssign);
        } catch (NotFoundException ex) {
            return ex.getMessage();
        }
    }

    /**
     * Validates the last parsed document.
     *
     * @return "Json is valid" if no parse errors were found or the stored error message otherwise
     */
    public String validate() {


        JsonParseResult jsonParseResult = JsonParser.parseJson(rawText);

        if (rawText == null) {
            return "You must first open a file before validating";
        }
        if(jsonParseResult.isSuccess())
        {
            jsonObject = jsonParseResult.parsedData;
            return "Json is valid";
        }
        else
        {
            return jsonParseResult.errorMessage;
        }

    }

    /**
     * Finds all values whose key matches {@code key} anywhere in the document.
     *
     * @param key property name
     * @return formatted list or not-found message
     */
    public String search(String key) {

        if(jsonObject == null)
            return "No file opened";

        JsonArray valuesFound = jsonObject.search(key);
        return valuesFound.isEmpty() ? "No values with the key \"" + key + "\"" : formatStructuredJson(valuesFound, 1);
    }


    /**
     * @return pretty-printed version of the current JSON
     */
    public String getStructuredJson() {
        if(jsonObject == null)
            return "No file opened";
        return formatStructuredJson(jsonObject, 0);
    }

    /**
     * Pretty prints a JSON subtree with indentation.
     *
     * @param json  element to print
     * @param depth current indentation level (1 = root)
     * @return string representation with line breaks and tabs
     */
    private static String formatStructuredJson(JsonElement json, int depth) {
       return json.toJson(depth);
    }

    /**
     * Saves the whole document to {@code fileName}.
     *
     * @param fileName destination path
     * @return empty string on success or error message
     */
    public String saveAs(String fileName) {
        if(jsonObject == null)
            return "No file opened";
        return saveAs(fileName, jsonObject);
    }

    /**
     * Saves {@code jsonObject} to {@code fileName}.
     *
     * @param fileName destination path
     * @param jsonObject subtree to write
     * @return empty string on success or error message
     */
    private String saveAs(String fileName, JsonElement jsonObject) {
        Path path = Path.of(fileName);
        try {
            try {
                Files.createFile(path);
            } catch (FileAlreadyExistsException ignored) {
            }
            Files.writeString(path, formatStructuredJson(jsonObject, 0));
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }

        return "File saved";
    }

    /**
     * Writes the subtree at {@code path} to {@code fileName}.
     *
     * @param fileName target path
     * @param path     JSON path inside the current document
     * @return result message
     */
    public String saveAs(String fileName, String path) {
       if(rawText == null)
           return "No file opened";

        if(jsonObject == null)
            return "Json not validated";
        
        JsonElement jsonElement;
        try {
            jsonElement = jsonObject.getValueAt(parseJsonPath(path), "Main object");
        } catch (NotFoundException ex) {
            return ex.getMessage();
        }
        return saveAs(fileName, jsonElement);
    }



    /**
     * Closes the file and clears the in-memory state.
     *
     * @return confirmation message
     */
    public String close() {
        if(rawText == null)
            return "No file opened";
        jsonObject = null;
        filePath = null;
        rawText = null;
        return "File closed";
    }

    /**
     * Creates missing path elements then writes {@code jsonValue} there.
     *
     * @param path      destination path
     * @param jsonValue JSON literal or fragment to insert
     * @return result message
     */
    public String create(String path, String jsonValue) {
        if(jsonObject == null)
            return "No file opened";
        JsonParseResult parseResult = JsonParser.parseJson(jsonValue);
        if (!parseResult.isSuccess()) {
            return "Error in parsing <to> parameter: " + parseResult.errorMessage;
        }

        JsonElement value = parseResult.parsedData;
        Queue<String> pathQueue = parseJsonPath(path);

        try {
            JsonElement copy = jsonObject.deepCopy();
            copy.completePath(pathQueue);
            jsonObject = copy;
        } catch (InvalidJsonPathException e) {
            return e.getMessage();
        }

        try {
            assign(parseJsonPath(path), jsonObject, "Main object", value);
        } catch (NotFoundException ex) {
            return ex.getMessage();
        }

        return "Element created";
    }

    /**
     * Saves the document to the originally opened file.
     *
     * @return empty string on success or error message
     */
    public String save() {
        return filePath != null ? saveAs(filePath.toString()) : "No file open";
    }
}
