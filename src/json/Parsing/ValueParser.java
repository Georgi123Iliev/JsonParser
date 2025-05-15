package src.json.Parsing;

public class ValueParser {

    public static ValueParseResult parseValue(String input) throws IllegalArgumentException{
        if (input == null || input.trim().equalsIgnoreCase("null")) {
            return null;
        }

        input = input.trim();

        if (input.startsWith("\"") && input.endsWith("\"") && input.length() >= 2) {

            return new ValueParseResult(input.substring(1, input.length() - 1));
        }
        try {
            return new ValueParseResult(Integer.parseInt(input));
        } catch (NumberFormatException ignored) {}


        try {
            return new ValueParseResult(Double.parseDouble(input));
        } catch (NumberFormatException ignored) {}


        return ValueParseResult.FailedParse();
    }
}

