package src.validators;

public class ValidationResult {

    private boolean isSuccess;
    private String reasonForFailure;

    private ValidationResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public ValidationResult(String reasonForFailure) {
        isSuccess = false;
        this.reasonForFailure = reasonForFailure;
    }

    public String getReasonForFailure() {
        return reasonForFailure;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public static ValidationResult success()
    {
        return new ValidationResult(true);
    }

}
