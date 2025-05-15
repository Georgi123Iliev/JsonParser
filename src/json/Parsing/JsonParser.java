package src.json.Parsing;

import src.exception.JsonParseException;
import src.json.types.*;

import java.util.*;
import java.util.regex.*;

public class JsonParser {
    private static int index;
    private static final Pattern STRING_PATTERN = Pattern.compile("\"(\\\\.|[^\"])*\"");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?");
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile("true|false");
    private static final Pattern NULL_PATTERN = Pattern.compile("null");


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
        }catch (JsonParseException ex)
        {
            return new JsonParseResult(ex.getMessage(),ex.getPosition());
        }

        if (index != json.length()) {
            return new JsonParseResult("Unexpected character at position " + index + " before " + json.substring(index), index);
        }
        return new JsonParseResult(result);
    }

    private static JsonElement parseObject(String json) throws JsonParseException {
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

    private static JsonElement parseArray(String json) throws JsonParseException{
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

    private static JsonElement parseValue(String json) throws JsonParseException {
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

    private static JsonElement parseString(String json) throws JsonParseException {
        Matcher matcher = STRING_PATTERN.matcher(json.substring(index));
        if (!matcher.lookingAt()) {
            throw new JsonParseException("Invalid string at position " + index, index);
        }
        String result = matcher.group().substring(1, matcher.group().length() - 1);
        index += matcher.end();
        return new JsonString(result.replace("\\\"", "\"").replace("\\\\", "\\"));
    }

    private static JsonParseResult checkBraces(String json) {
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

    private static int skipWhitespace(String json, int i) {
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }
        return i;
    }
}
