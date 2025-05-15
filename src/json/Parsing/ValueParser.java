package src.json.Parsing;

import src.json.types.JsonNumber;
import src.json.types.JsonString;


public class ValueParser {

    /**
     *
     * @param input
     * @return
     * @throws IllegalArgumentException
     */
    public static ValueParseResult parseValue(String input) throws IllegalArgumentException{
        if (input == null || input.trim().equalsIgnoreCase("null")) {
            return null;
        }

        input = input.trim();

        if (input.startsWith("\"") && input.endsWith("\"") && input.length() >= 2) {

            return new ValueParseResult(new JsonString(input.substring(1, input.length() - 1)));
        }
        try {
            return new ValueParseResult(new JsonNumber(Integer.parseInt(input)));
        } catch (NumberFormatException ignored) {}


        try {
            return new ValueParseResult(new JsonNumber(Double.parseDouble(input)));
        } catch (NumberFormatException ignored) {}


        return ValueParseResult.FailedParse();
    }
}

