package src.validators;

public class JsonPathIntersectionValidator {
    public static ValidationResult Validate(String from,String to)
    {
        from = from.trim();
        to = to.trim();
        if(to.indexOf(from) == 0)
        {
            return new ValidationResult("Премахването на " + from + " ще премахне и " + to);
        }

        return ValidationResult.success();
    }

}
