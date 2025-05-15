package src.validators;

public class JsonPathIntersectionValidator {
    public static ValidationResult validate(String from, String to)
    {
        from = from.trim();
        to = to.trim();
        if(to.indexOf(from) == 0)
        {
            return new ValidationResult("Removing  " + from + " would also remove " + to);
        }

        return ValidationResult.success();
    }

}
