package src.json.Parsing;

import src.json.types.JsonNumber;
import src.json.types.JsonString;

/**
 * Parses a single JSON literal (string, integer, double, or the token {@code null}).
 */
public class ValueParser {

    /**
     * Attempts to interpret {@code input} as a JSON scalar.
     * <ul>
     *   <li>If {@code input} equals {@code null} (ignoring case and whitespace), the method returns {@code null}.</li>
     *   <li>Double‑quoted text becomes a {@link JsonString}.</li>
     *   <li>A whole number becomes a {@link JsonNumber} backed by {@code Integer}.</li>
     *   <li>A decimal number becomes a {@link JsonNumber} backed by {@code Double}.</li>
     *   <li>If none match, {@link ValueParseResult#FailedParse()} is returned.</li>
     * </ul>
     *
     * @param input raw token to parse
     * @return a {@link ValueParseResult} describing the outcome, or {@code null} when the token represents JSON {@code null}
     * @throws IllegalArgumentException not thrown by the current implementation, reserved for future validation needs
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
