package src.validators;

/**
 * Represents the result of a validation operation.
 */
public class ValidationResult {

    private boolean isSuccess;
    private String reasonForFailure;

    /**
     * Creates a successful result.
     *
     * @param isSuccess must be true to indicate success
     */
    private ValidationResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * Creates a failed result with an explanation.
     *
     * @param reasonForFailure reason why the validation failed
     */
    public ValidationResult(String reasonForFailure) {
        isSuccess = false;
        this.reasonForFailure = reasonForFailure;
    }

    /**
     * @return explanation for failure, or null if successful
     */
    public String getReasonForFailure() {
        return reasonForFailure;
    }

    /**
     * @return true if the validation succeeded
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * @return a successful ValidationResult
     */
    public static ValidationResult success()
    {
        return new ValidationResult(true);
    }

}