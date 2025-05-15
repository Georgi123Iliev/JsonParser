package src.json.Parsing;

public class ValueParseResult {
    public boolean isSuccess;
    public Object parsedValue;

    public ValueParseResult(Object parsedValue) {
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
