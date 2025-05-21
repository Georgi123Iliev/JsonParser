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
    private JsonParseResult jsonParseResult;
    private boolean isSaved;

    /** Creates an empty handler with no file loaded. */
    public JsonFileHandler() {}

    /**
     * Opens a JSON file, parses it and stores the result in memory.
     *
     * @param fileName path to the file on disk
     * @return empty string on success, otherwise an explanatory message
     */
    public String openFile(String fileName) {
        if (jsonObject != null && !isSaved) {
            return "A file is currently opened and not been saved.";
        }

        jsonObject = null;
        isSaved = false;
        Path path = Path.of(fileName);

        if (!Files.exists(path)) {
           saveAs(fileName,new JsonObject());
        }

        try {
            String rawText = Files.readString(path);
            filePath = path;
            jsonParseResult = JsonParser.parseJson(rawText);
            if (jsonParseResult.isSuccess()) {
                jsonObject = jsonParseResult.parsedData;
            }
            isSaved = true;
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }

        return "";
    }

    /**
     * Deletes the element located at {@code jsonPath}.
     *
     * @param jsonPath dot / bracket path, e.g. {@code root.array[0]}
     * @return result message
     */
    public String remove(String jsonPath) {
        Queue<String> pathQueue = parseJsonPath(jsonPath);
        try {
            return assign(pathQueue, jsonObject, null, Nothing.INSTANCE);
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
        if (jsonPath.isEmpty()) {
            return "Empty JSON path";
        }

        String head = jsonPath.poll();

        if (currentData instanceof JsonArray jsonArray) {
            if (!head.matches("\\[\\d+\\]")) {
                return "In array " + previousKey + ", '" + head + "' is not a valid index format";
            }

            int index = Integer.parseInt(head.substring(1, head.length() - 1));
            if (index >= jsonArray.size()) {
                return head + " is greater than the number of items in the array " + previousKey;
            }

            if (jsonPath.isEmpty()) {
                if (valueToAdd instanceof Nothing) {
                    jsonArray.remove(index);
                    return "Element removed";
                } else {
                    jsonArray.set(index, valueToAdd);
                    return "Element set";
                }
            }

            return assign(jsonPath, jsonArray.get(index), head, valueToAdd);

        } else if (currentData instanceof JsonObject jsonObject) {
            if (!jsonObject.containsKey(head)) {
                return "Json path error: '" + previousKey + "' has no key '" + head + "'";
            }

            if (jsonPath.isEmpty()) {
                if (valueToAdd instanceof Nothing) {
                    jsonObject.remove(head);
                    return "Element removed";
                } else {
                    jsonObject.add(head, valueToAdd);
                    return "Element set";
                }
            }

            return assign(jsonPath, jsonObject.get(head), head, valueToAdd);

        } else {
            return "Invalid JSON structure: Cannot descend into primitive at " + previousKey;
        }
    }

    /**
     * Moves data from {@code from} to {@code to} after validating that the paths do not overlap.
     *
     * @param from source path
     * @param to   destination path
     * @return result message
     */
    public String move(String from, String to) {
        var validationResult = JsonPathIntersectionValidator.validate(from, to);
        if (!validationResult.isSuccess()) {
            return validationResult.getReasonForFailure();
        }

        Queue<String> fromQueue = parseJsonPath(from);
        JsonElement value;
        try {
            value = getValueAt(fromQueue, jsonObject, "");
            remove(from);
            return assign(parseJsonPath(to), jsonObject, "", value);
        } catch (NotFoundException ex) {
            return ex.getMessage();
        }
    }

    /**
     * Returns the element referenced by {@code jsonPath}.
     *
     * @param jsonPath    queue of path tokens (consumed)
     * @param currentData traversal cursor
     * @param previousKey last key/index for error context
     * @return the element found
     * @throws NotFoundException if the path is invalid
     */
    private JsonElement getValueAt(Queue<String> jsonPath, JsonElement currentData, String previousKey) throws NotFoundException {
        if (jsonPath.isEmpty()) {
            throw new NotFoundException("Empty JSON path");
        }

        String head = jsonPath.poll();

        if (currentData instanceof JsonArray jsonArray) {
            if (!head.matches("\\[\\d+\\]")) {
                throw new NotFoundException("In array " + previousKey + ", '" + head + "' is not a valid index");
            }

            int index = Integer.parseInt(head.substring(1, head.length() - 1));
            if (index >= jsonArray.size()) {
                throw new NotFoundException(head + " is greater than the number of items in array " + previousKey);
            }

            JsonElement element = jsonArray.get(index);
            return jsonPath.isEmpty() ? element : getValueAt(jsonPath, element, head);

        } else if (currentData instanceof JsonObject jsonObject) {
            if (!jsonObject.containsKey(head)) {
                throw new NotFoundException("JSON object '" + previousKey + "' has no key '" + head + "'");
            }

            JsonElement element = jsonObject.get(head);
            return jsonPath.isEmpty() ? element : getValueAt(jsonPath, element, head);

        } else {
            throw new NotFoundException("Invalid JSON structure at '" + previousKey + "', cannot descend into primitive");
        }
    }

    /**
     * Converts the remaining queue back into a human-readable path (used for error messages).
     *
     * @param path queue of tokens
     * @return concatenated path string
     */
    private String jsonPathToString(Queue<String> path) {
        StringBuilder sb = new StringBuilder();
        for (String segment : path) {
            sb.append(segment);
        }
        return sb.toString();
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
        ValueParseResult valueParseResult = ValueParser.parseValue(jsonValue);
        JsonParseResult parseResult = JsonParser.parseJson(jsonValue);

        if (!valueParseResult.isSuccess) {
            return parseResult.isSuccess() ? parseResult.errorMessage : "Bad json string";
        }

        if (!parseResult.isSuccess()) {
            return parseResult.errorMessage;
        }

        try {
            return assign(parseJsonPath(jsonPath), jsonObject, null, parseResult.parsedData);
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
        if (jsonParseResult == null) {
            return "You must first open a file before validating";
        }
        return jsonParseResult.isSuccess() ? "Json is valid" : jsonParseResult.errorMessage;
    }

    /**
     * Finds all values whose key matches {@code key} anywhere in the document.
     *
     * @param key property name
     * @return formatted list or not-found message
     */
    public String search(String key) {
        JsonArray valuesFound = searchKey(key, jsonObject);
        return valuesFound.isEmpty() ? "No values with the key \"" + key + "\"" : formatStructuredJson(valuesFound, 1);
    }

    /**
     * Recursive helper for {@link #search(String)}.
     *
     * @param key  property name to look for
     * @param data current subtree
     * @return array of found values (may be empty)
     */
    private JsonArray searchKey(String key, JsonElement data) {
        JsonArray values = new JsonArray();

        if (data instanceof JsonObject jsonObject) {
            for (String k : jsonObject.keySet()) {
                JsonElement value = jsonObject.get(k);
                if (k.equals(key)) {
                    values.add(value);
                }
                values.addAll((JsonArray) searchKey(key, value));
            }
        } else if (data instanceof JsonArray jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                values.addAll((JsonArray) searchKey(key, jsonArray.get(i)));
            }
        }

        return values;
    }

    /**
     * @return pretty-printed version of the current JSON
     */
    public String getStructuredJson() {
        return formatStructuredJson(jsonObject, 1);
    }

    /**
     * Pretty prints a JSON subtree with indentation.
     *
     * @param json  element to print
     * @param depth current indentation level (1 = root)
     * @return string representation with line breaks and tabs
     */
    private static String formatStructuredJson(JsonElement json, int depth) {
        StringBuilder sb = new StringBuilder();
        String padding = "\t".repeat(Math.max(0, depth - 1));
        String innerPadding = "\t".repeat(depth);

        if (json instanceof JsonObject obj) {
            sb.append(padding).append("{\n");
            int count = 0;
            int total = obj.keySet().size();
            for (String key : obj.keySet()) {
                JsonElement value = obj.get(key);
                sb.append(innerPadding).append("\"").append(key).append("\" : ");
                if (value instanceof JsonObject || value instanceof JsonArray) {
                    sb.append("\n");
                }
                sb.append(formatStructuredJson(value, depth + 1));
                sb.append(++count != total ? ",\n" : "\n");
            }
            sb.append(padding).append("}");
        } else if (json instanceof JsonArray arr) {
            sb.append(padding).append("[");
            if (arr.size() > 0) {
                sb.append("\n");
            }
            for (int i = 0; i < arr.size(); i++) {
                JsonElement value = arr.get(i);
                if (!(value instanceof JsonObject || value instanceof JsonArray)) {
                    sb.append(innerPadding);
                }
                sb.append(formatStructuredJson(value, depth + 1));
                sb.append(i != arr.size() - 1 ? ",\n" : "\n");
            }
            sb.append(padding).append("]");
        } else {
            sb.append(json);
        }

        return sb.toString();
    }

    /**
     * Saves the whole document to {@code fileName}.
     *
     * @param fileName destination path
     * @return empty string on success or error message
     */
    public String saveAs(String fileName) {
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
            Files.writeString(path, formatStructuredJson(jsonObject, 1));
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
        isSaved = true;
        return "";
    }

    /**
     * Writes the subtree at {@code path} to {@code fileName}.
     *
     * @param fileName target path
     * @param path     JSON path inside the current document
     * @return result message
     */
    public String saveAs(String fileName, String path) {
        JsonElement jsonElement;
        try {
            jsonElement = getValueAt(parseJsonPath(path), jsonObject, null);
        } catch (NotFoundException ex) {
            return ex.getMessage();
        }
        return saveAs(fileName, jsonElement);
    }

    /**
     * Ensures all intermediate objects/arrays along {@code jsonPath} exist, creating them as needed.
     *
     * @param jsonPath    queue of tokens
     * @param currentData traversal cursor
     * @throws InvalidJsonPathException if the path is internally inconsistent
     */
    private void completePath(Queue<String> jsonPath, JsonElement currentData) throws InvalidJsonPathException {
        String head = jsonPath.poll();
        String next = jsonPath.peek();

        if (head == null) {
            return;
        }

        JsonElement nextElement = (next == null) ? null : (next.matches("\\[\\d+\\]") ? new JsonArray() : new JsonObject());

        if (currentData instanceof JsonArray jsonArray) {
            if (!head.matches("\\[\\d+\\]")) {
                throw new InvalidJsonPathException("Expected array index but got object key '" + head + "'");
            }

            int index = Integer.parseInt(head.substring(1, head.length() - 1));
            if (index == jsonArray.size()) {
                jsonArray.add(nextElement);
            } else if (index > jsonArray.size()) {
                throw new InvalidJsonPathException("Cannot complete path: trying to add index " + index + " before completing previous indices.");
            }

            completePath(jsonPath, jsonArray.get(index));

        } else if (currentData instanceof JsonObject jsonObject) {
            if (head.matches("\\[\\d+\\]")) {
                throw new InvalidJsonPathException("Unexpected array index '" + head + "' in object path");
            }

            if (!jsonObject.containsKey(head)) {
                jsonObject.add(head, nextElement);
            }

            if (!jsonPath.isEmpty()) {
                completePath(jsonPath, jsonObject.get(head));
            }

        } else {
            throw new InvalidJsonPathException("Invalid element encountered during path completion.");
        }
    }

    /**
     * Closes the file and clears the in-memory state.
     *
     * @return confirmation message
     */
    public String close() {
        jsonObject = null;
        isSaved = false;
        filePath = null;
        jsonParseResult = null;
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
        JsonParseResult parseResult = JsonParser.parseJson(jsonValue);
        if (!parseResult.isSuccess()) {
            return "Error in parsing <to> parameter: " + parseResult.errorMessage;
        }

        JsonElement value = parseResult.parsedData;
        Queue<String> pathQueue = parseJsonPath(path);

        try {
            JsonElement copy = jsonObject.deepCopy();
            completePath(pathQueue, copy);
            jsonObject = copy;
        } catch (InvalidJsonPathException e) {
            return e.getMessage();
        }

        try {
            assign(parseJsonPath(path), jsonObject, null, value);
        } catch (NotFoundException ex) {
            return ex.getMessage();
        }

        return "";
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
