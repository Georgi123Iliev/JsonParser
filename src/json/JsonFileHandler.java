package src.json;

import src.exception.InvalidJsonPathException;
import src.exception.NotFoundException;
import src.json.Parsing.JsonParseResult;
import src.json.Parsing.JsonParser;
import src.json.Parsing.ValueParseResult;
import src.json.Parsing.ValueParser;
import src.json.commands.SaveAs;
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

public class JsonFileHandler {

    private Path filePath;
    private JsonElement jsonObject;
    private JsonParseResult jsonParseResult;
    private boolean isSaved;

    public JsonFileHandler() {}

    public String openFile(String fileName) {
        if (jsonObject != null && !isSaved) {
            return "A file is currently opened and not been saved.";
        }

        jsonObject = null;
        isSaved = false;
        Path path = Path.of(fileName);

        if (!Files.exists(path)) {
            return "File does not exist";
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


    public String remove(String jsonPath) {
        Queue<String> pathQueue = parseJsonPath(jsonPath);
        try {


            return assign(pathQueue, jsonObject, null, Nothing.INSTANCE);
        }catch (NotFoundException ex)
        {
            return ex.getMessage();
        }
    }

    private String assign(Queue<String> jsonPath, JsonElement currentData, String previousKey, JsonElement valueToAdd) throws NotFoundException {
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
                    jsonArray.remove(index);  // If getElements() is not exposed, add a `remove(int)` method
                    return "Element removed";
                } else {
                    jsonArray.set(index, valueToAdd);  // Or use a `set(int, JsonElement)` method
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
        }catch (NotFoundException ex)
        {
            return ex.getMessage();
        }



    }


    private JsonElement getValueAt(Queue<String> jsonPath,JsonElement currentData, String previousKey) throws NotFoundException
    {

        if (jsonPath.isEmpty()) {
            throw new NotFoundException("Empty JSON path");
        }

        String head = jsonPath.poll();

        if (currentData instanceof JsonArray jsonArray) {

            if (!head.matches("\\[\\d+\\]")) {
                throw new NotFoundException(
                        "In array " + previousKey + ", '" + head + "' is not a valid index");
            }

            int index = Integer.parseInt(head.substring(1, head.length() - 1));
            if (index >= jsonArray.size()) {
                throw new NotFoundException(
                        head + " is greater than the number of items in array " + previousKey);
            }

            JsonElement element = jsonArray.get(index);
            return jsonPath.isEmpty()
                    ? element
                    : getValueAt(jsonPath, element, head);

        } else if (currentData instanceof JsonObject jsonObject) {

            if (!jsonObject.containsKey(head)) {
                throw new NotFoundException(
                        "JSON object '" + previousKey + "' has no key '" + head + "'");
            }

            JsonElement element = jsonObject.get(head);
            return jsonPath.isEmpty()
                    ? element
                    : getValueAt(jsonPath, element, head);

        } else {
            throw new NotFoundException(
                    "Invalid JSON structure at '" + previousKey + "', cannot descend into primitive");
        }
    }



    private String jsonPathToString(Queue<String> path) {
        StringBuilder sb = new StringBuilder();
        for (var segment : path) sb.append(segment);
        return sb.toString();
    }

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
                } else break;
            }
            if (!wasSplit) pathQueue.add(segment);
        }
        return pathQueue;
    }

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
        }catch (NotFoundException ex)
        {
            return ex.getMessage();
        }

    }

    public String validate() {
        if (jsonParseResult == null) return "You must first open a file before validating";
        return jsonParseResult.isSuccess() ? "Json is valid" : jsonParseResult.errorMessage;
    }

    public String search(String key) {
        JsonArray valuesFound = searchKey(key, jsonObject);
        return valuesFound.isEmpty()
                ? "No values with the key \"" + key + "\""
                : formatStructuredJson(valuesFound, 1);
    }

    private JsonArray searchKey(String key, JsonElement data) {
        JsonArray values = new JsonArray();

        if (data instanceof JsonObject jsonObject) {
            for (String k : jsonObject.keySet()) {
                JsonElement value = jsonObject.get(k);
                if (k.equals(key)) {
                    values.add(value);
                }
                values.addAll(((JsonArray) searchKey(key, value)));
            }
        } else if (data instanceof JsonArray jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                values.addAll(((JsonArray) searchKey(key, jsonArray.get(i))));
            }
        }

        return values;
    }


    public String getStructuredJson() {
        return formatStructuredJson(jsonObject, 1);
    }

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
            if (arr.size() > 0) sb.append("\n");
            for (int i = 0; i < arr.size(); i++) {
                JsonElement value = arr.get(i);
                if (!(value instanceof JsonObject || value instanceof JsonArray)) {
                    sb.append(innerPadding);
                }
                sb.append(formatStructuredJson(value, depth + 1));
                sb.append(i != arr.size() - 1 ? ",\n" : "\n");
            }
            sb.append(padding).append("]");
        }
        else
        {
            sb.append(json);
        }



        return sb.toString();
    }

    /**
     *
     * @param fileName
     * @return
     */
    public String saveAs(String fileName) {
       return saveAs(fileName,jsonObject);
    }

    /**
     *
     * @param fileName
     * @param jsonObject
     * @return
     */
    private String saveAs(String fileName,JsonElement jsonObject)
    {
        Path path = Path.of(fileName);
        try {
            try {
                Files.createFile(path);
            }catch (FileAlreadyExistsException ex){};


            Files.writeString(path,formatStructuredJson(jsonObject,1));
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }

        isSaved = true;
        return "";
    }
    public String saveAs(String fileName,String path)
    {
        JsonElement jsonElement;
        try {


            jsonElement = getValueAt(parseJsonPath(path),jsonObject,null);
        }catch (NotFoundException ex)
        {
            return ex.getMessage();
        }


        return saveAs(fileName,jsonElement);
    }

    private void completePath(Queue<String> jsonPath, JsonElement currentData) throws InvalidJsonPathException {
        String head = jsonPath.poll();
        String next = jsonPath.peek();

        if (head == null) return;

        JsonElement nextElement = (next == null)
                ? null
                : (next.matches("\\[\\d+\\]") ? new JsonArray() : new JsonObject());

        if (currentData instanceof JsonArray jsonArray) {
            if (!head.matches("\\[\\d+\\]")) {
                throw new InvalidJsonPathException("Expected array index but got object key '" + head + "' before " +
                        (jsonPathToString(jsonPath).isEmpty() ? "end of path" : jsonPathToString(jsonPath)));
            }

            int index = Integer.parseInt(head.substring(1, head.length() - 1));
            if (index == jsonArray.size()) {
                jsonArray.add(nextElement);
            } else if (index > jsonArray.size()) {
                throw new InvalidJsonPathException("Cannot complete path: trying to add index " + index +
                        " before completing previous indices.");
            }

            completePath(jsonPath, jsonArray.get(index));

        } else if (currentData instanceof JsonObject jsonObject) {
            if (head.matches("\\[\\d+\\]")) {
                throw new InvalidJsonPathException("Unexpected array index '" + head + "' in object path before " +
                        (jsonPathToString(jsonPath).isEmpty() ? "end of path" : jsonPathToString(jsonPath)));
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


    public String close() {

            jsonObject = null;
            isSaved = false;
            filePath = null;
            jsonParseResult = null;
            return "File closed";

    }

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
        }catch (NotFoundException ex)
        {
            return ex.getMessage();
        }

        return "";
    }


    public String save() {
        return filePath != null ? saveAs(filePath.toString()) : "No file open";
    }
}
