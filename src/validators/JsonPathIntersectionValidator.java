package src.validators;
/**
 * Utility that checks whether moving or deleting a JSON element would
 * inadvertently affect one of its own descendants (i.e., the <code>to</code>
 * path lies inside the <code>from</code> path).
 */



public class JsonPathIntersectionValidator {

    /**
     * Determines whether <code>from</code> and <code>to</code> intersect
     * such that removing/moving <code>from</code> would also remove/move
     * <code>to</code>.
     *
     * @param from the source JSON path (candidate for removal or move)
     * @param to   the destination or reference JSON path
     * @return a {@link ValidationResult} that is successful when the paths
     *         are independent, or failed with an explanatory message when
     *         <code>to</code> is a descendant of <code>from</code>
     */

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
