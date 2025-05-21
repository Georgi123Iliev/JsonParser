package src.json.Parsing;

import src.exception.JsonParseException;
import src.json.types.*;

import java.util.*;
import java.util.regex.*;

/**
 * Recursive‑descent parser for a limited JSON grammar.
 */
public class JsonParser {
    /** Current read position in the input. */
    private static int index;

    private static final Pattern STRING_PATTERN = Pattern.compile("\"(\\\\.|[^\"])*\"");
    private static final Pattern STRING_PATTERN2 = Pattern.compile("\"(\\\\.|[^'])*'");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?");
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile("true|false");
    private static final Pattern NULL_PATTERN = Pattern.compile("null");

    /**
     * Parses a JSON document.
     *
     * @param json the raw JSON text
     * @return {@link JsonParseResult} containing a parse tree or error information
     */
    public static JsonParseResult parseJson(String json) {
        index = 0;
        json = json.trim();
        JsonParseResult braceCheckResult = checkBraces(json);
        if (!braceCheckResult.isSuccess())
            return braceCheckResult;

        JsonElement result;
        try {
            result = parseValue(json);
            index = skipWhitespace(json, index);
        } catch (JsonParseException ex) {
            return new JsonParseResult(ex.getMessage(), ex.getPosition());
        }

        if (index != json.length()) {
            return new JsonParseResult("Unexpected character at position " + index + " before " + json.substring(index), index);
        }
        return new JsonParseResult(result);
    }

    /**
     * Parses an object starting at the current {@code index}.
     *
     * @param json the whole JSON string being parsed
     * @return a new {@link JsonObject}
     * @throws JsonParseException if an object member is malformed
     */
    private static  JsonElement parseObject(String json) throws JsonParseException {
        JsonObject obj = new JsonObject();
        index++;
        index = skipWhitespace(json, index);

        while (index < json.length() && json.charAt(index) != '}') {
            index = skipWhitespace(json, index);
            Matcher keyMatcher = STRING_PATTERN.matcher(json.substring(index));


            if (!keyMatcher.lookingAt()) {
                throw new JsonParseException("Expected string key at position " + index, index);
            }
            String key = keyMatcher.group().substring(1, keyMatcher.group().length() - 1);
            index += keyMatcher.end();

            index = skipWhitespace(json, index);
            if (json.charAt(index) != ':') {
                throw new JsonParseException("Expected ':' at position " + index, index);
            }
            index++;

            index = skipWhitespace(json, index);
            JsonElement value = parseValue(json);
            obj.add(key, value);

            index = skipWhitespace(json, index);
            if (json.charAt(index) == ',') {
                index++;
            } else if (json.charAt(index) != '}') {
                throw new JsonParseException("Expected ',' or '}' at position " + index, index);
            }
        }

        if (index >= json.length() || json.charAt(index) != '}') {
            throw new JsonParseException("Expected '}' at position " + index, index);
        }
        index++;
        return obj;
    }

    /**
     * Parses an array starting at the current {@code index}.
     *
     * @param json the whole JSON string being parsed
     * @return a new {@link JsonArray}
     * @throws JsonParseException if the array structure is invalid
     */
    private  static  JsonElement parseArray(String json) throws JsonParseException {
        JsonArray array = new JsonArray();
        index++;
        index = skipWhitespace(json, index);

        while (index < json.length() && json.charAt(index) != ']') {
            JsonElement value = parseValue(json);
            array.add(value);

            index = skipWhitespace(json, index);

            if (json.charAt(index) == ',') {
                index++;
            } else if (json.charAt(index) != ']') {
                throw new JsonParseException("Expected ',' or ']' at position " + index, index);
            }
        }

        if (index >= json.length() || json.charAt(index) != ']') {
            throw new JsonParseException("Expected ']' at position " + index, index);
        }
        index++;
        return array;
    }

    /**
     * Dispatches to the appropriate value parser based on the next character.
     *
     * @param json the whole JSON string being parsed
     * @return a parsed {@link JsonElement}
     * @throws JsonParseException if the value is ill‑formed or unsupported
     */
    private static   JsonElement parseValue(String json) throws JsonParseException {
        index = skipWhitespace(json, index);
        if (index >= json.length()) {
            throw new JsonParseException("Unexpected end of JSON", index);
        }

        char c = json.charAt(index);

        if (c == '"') return parseString(json);
        if (c == '{') return parseObject(json);
        if (c == '[') return parseArray(json);

        Matcher numMatcher = NUMBER_PATTERN.matcher(json.substring(index));
        if (numMatcher.lookingAt()) {
            String numStr = numMatcher.group();
            index += numMatcher.end();
            return new JsonNumber(numStr.contains(".") ? Double.parseDouble(numStr) : Integer.parseInt(numStr));
        }

        Matcher boolMatcher = BOOLEAN_PATTERN.matcher(json.substring(index));
        if (boolMatcher.lookingAt()) {
            String boolStr = boolMatcher.group();
            index += boolMatcher.end();
            return new JsonBoolean(Boolean.parseBoolean(boolStr));
        }

        Matcher nullMatcher = NULL_PATTERN.matcher(json.substring(index));
        if (nullMatcher.lookingAt()) {
            index += nullMatcher.end();
            return JsonNull.INSTANCE;
        }

        throw new JsonParseException("Invalid value at position " + index, index);
    }

    /**
     * Parses a JSON string literal.
     *
     * @param json the whole JSON string being parsed
     * @return a {@link JsonString} instance
     * @throws JsonParseException if the string is not properly delimited
     */
    private static   JsonElement parseString(String json) throws JsonParseException {
        Matcher matcher = STRING_PATTERN.matcher(json.substring(index));
        if (!matcher.lookingAt()) {
            throw new JsonParseException("Invalid string at position " + index, index);
        }
        String result = matcher.group().substring(1, matcher.group().length() - 1);
        index += matcher.end();
        return new JsonString(result.replace("\\\"", "\"").replace("\\\\", "\\"));
    }

    /**
     * Checks that all brackets and braces are balanced.
     *
     * @param json the input to verify
     * @return a successful {@link JsonParseResult} if balanced, otherwise an error
     */
    private static    JsonParseResult checkBraces(String json) {
        Stack<Character> braces = new Stack<>();
        int lastBraceIndex = 0;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            String errorMessage = "Mismatched brace of type " + c + " at " + i + " before " + json.substring(i, Math.min(json.length(), i + 30));

            if (c == '}') {
                if (!braces.isEmpty() && braces.peek() == '{') {
                    braces.pop();
                } else {
                    return new JsonParseResult(errorMessage, i);
                }
            }

            if (c == ']') {
                if (!braces.isEmpty() && braces.peek() == '[') {
                    braces.pop();
                } else {
                    return new JsonParseResult(errorMessage, i);
                }
            }

            if (c == '{' || c == '[') {
                braces.push(c);
                lastBraceIndex = i;
            }
        }

        if (braces.isEmpty()) {
            return new JsonParseResult(new JsonBoolean(true));
        } else {
            return new JsonParseResult("Unclosed brace " + braces.peek() + " at " + lastBraceIndex + " before " + json.substring(lastBraceIndex, Math.min(json.length(), lastBraceIndex + 30)), lastBraceIndex);
        }
    }

    /**
     * Moves the cursor past consecutive whitespace.
     *
     * @param json the input string
     * @param i    starting position
     * @return first index that is not whitespace
     */
    private static  int skipWhitespace(String json, int i) {
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }
        return i;
    }
}
