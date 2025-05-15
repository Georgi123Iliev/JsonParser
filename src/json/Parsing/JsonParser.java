package src.json.Parsing;

import java.util.*;
import java.util.regex.*;

public class JsonParser {
    private static int index;
    private static final Pattern STRING_PATTERN = Pattern.compile("\"(\\\\.|[^\"])*\"");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?");
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile("true|false");
    private static final Pattern NULL_PATTERN = Pattern.compile("null");

    public static JsonParseResult parseJson(String json) {
        index = 0; // Reset index before parsing
        json = json.trim();
        JsonParseResult braceCheckResult = checkBraces(json);
        if(!braceCheckResult.isSuccess())
            return braceCheckResult;

        Object result = parseValue(json);
        index = skipWhitespace(json, index);

        if (index != json.length()) {
            return new JsonParseResult("Unexpected character at position " + index + " before "
                    + json.substring(index), index);
        }
        return new JsonParseResult(result);
    }

    private static Object parseObject(String json) {
        Map<String, Object> map = new HashMap<>();
        index++; // Skip '{'
        index = skipWhitespace(json, index);

        while (index < json.length() && json.charAt(index) != '}') {
            index = skipWhitespace(json, index);
            Matcher keyMatcher = STRING_PATTERN.matcher(json.substring(index));
            if (!keyMatcher.lookingAt()) {
                return new JsonParseResult("Expected string key at position " + index, index);
            }
            String key = keyMatcher.group().substring(1, keyMatcher.group().length() - 1);
            index += keyMatcher.end();

            index = skipWhitespace(json, index);
            if (json.charAt(index) != ':') {
                return new JsonParseResult("Expected ':' at position " + index, index);
            }
            index++; // Skip ':'

            index = skipWhitespace(json, index);
            Object value = parseValue(json);
            map.put(key, value);

            index = skipWhitespace(json, index);
            if (json.charAt(index) == ',') {
                index++; // Skip ','
            } else if (json.charAt(index) != '}') {
                return new JsonParseResult("Expected ',' or '}' at position " + index, index);
            }
        }

        if (index >= json.length() || json.charAt(index) != '}') {
            return new JsonParseResult("Expected '}' at position " + index, index);
        }
        index++; // Skip '}'
        return map;
    }

    private static Object parseArray(String json) {
        List<Object> list = new ArrayList<>();
        index++; // Skip '['
        index = skipWhitespace(json, index);

        while (index < json.length() && json.charAt(index) != ']') {
            Object value = parseValue(json);
            list.add(value);

            index = skipWhitespace(json, index);

            if (json.charAt(index) == ',') {
                index++; // Skip ','
            } else if (json.charAt(index) != ']') {
                return new JsonParseResult("Expected ',' or ']' at position " + index, index);
            }


        }

        if (index >= json.length() || json.charAt(index) != ']') {
            return new JsonParseResult("Expected ']' at position " + index, index);
        }
        index++; // Skip ']'
        return list;
    }

    private static Object parseValue(String json) {
        index = skipWhitespace(json, index);
        if (index >= json.length()) {
            return new JsonParseResult("Unexpected end of JSON", index);
        }

        char c = json.charAt(index);

        if (c == '"') return parseString(json);
        if (c == '{') return parseObject(json);
        if (c == '[') return parseArray(json);

        Matcher numMatcher = NUMBER_PATTERN.matcher(json.substring(index));
        if (numMatcher.lookingAt()) {
            String numStr = numMatcher.group();
            index += numMatcher.end();
            return numStr.contains(".") ? Double.parseDouble(numStr) : Integer.parseInt(numStr);
        }

        Matcher boolMatcher = BOOLEAN_PATTERN.matcher(json.substring(index));
        if (boolMatcher.lookingAt()) {
            index += boolMatcher.end();
            return Boolean.parseBoolean(boolMatcher.group());
        }

        Matcher nullMatcher = NULL_PATTERN.matcher(json.substring(index));
        if (nullMatcher.lookingAt()) {
            index += nullMatcher.end();
            return null;
        }

        return new JsonParseResult("Invalid value at position " + index, index);
    }

    private static Object parseString(String json) {
        Matcher matcher = STRING_PATTERN.matcher(json.substring(index));
        if (!matcher.lookingAt()) {
            return new JsonParseResult("Invalid string at position " + index, index);
        }
        String result = matcher.group().substring(1, matcher.group().length() - 1); // Remove quotes
        index += matcher.end();
        return result.replace("\\\"", "\"").replace("\\\\", "\\"); // Handle escape sequences
    }


    private static JsonParseResult checkBraces(String json)
    {
        Stack<Character> braces = new Stack<>();

        int lastBraceIndex = 0;
        for (int i=0; i<json.length(); i++)
        {
            char c = json.charAt(i);

            String errorMessage = "Mismatched brace of type " + c + " at " + i + " before " + json.substring(i, Math.min(json.length(), i + 30));
            if(c == '}')
            {
                if(braces.peek() == '{')
                {
                    braces.pop();
                }
                else
                {
                    return new JsonParseResult(errorMessage,i);
                }
            }
            if( c == ']')
            {
                if(braces.peek() == '[')
                {
                    braces.pop();
                }
                else
                {
                    return new JsonParseResult(errorMessage,i);
                }
            }

            if(c=='{'||c=='[') {
                braces.push(c);
                lastBraceIndex = i;
            }
        }

        if(braces.isEmpty())
         return new JsonParseResult(new Object());
        else
        {
               return new JsonParseResult("Unclosed brace "+braces.peek() +" at "+lastBraceIndex + " before " +json.substring(lastBraceIndex,Math.min(json.length(),lastBraceIndex+30)),lastBraceIndex);
        }
    }

    private static int skipWhitespace(String json, int i) {
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }
        return i;
    }


}
