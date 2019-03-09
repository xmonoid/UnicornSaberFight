package vrn.edinorog.exception;

public class ApplicationException extends RuntimeException {

    private String possibleSolution;

    public ApplicationException() {
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, String possibleSolution) {
        super(message);
        this.possibleSolution = possibleSolution;
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getPossibleSolution() {
        return possibleSolution;
    }

}