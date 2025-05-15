package src.json.Parsing;

import src.json.types.JsonElement;

public class ValueParseResult {
    public boolean isSuccess;
    public JsonElement parsedValue;

    public ValueParseResult(JsonElement parsedValue) {
        isSuccess = true;
        this.parsedValue = parsedValue;
    }

   public static ValueParseResult FailedParse()
   {
       return new ValueParseResult();
   }
   private ValueParseResult()
   {
       isSuccess = false;

   }
}
