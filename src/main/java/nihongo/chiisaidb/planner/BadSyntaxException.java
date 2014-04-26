package nihongo.chiisaidb.planner;

/**
 * A runtime exception indicating that the submitted query has incorrect syntax.
 */
@SuppressWarnings("serial")
public class BadSyntaxException extends RuntimeException {
	public BadSyntaxException() {
	}

	public BadSyntaxException(String message) {
		super(message);
	}
}
