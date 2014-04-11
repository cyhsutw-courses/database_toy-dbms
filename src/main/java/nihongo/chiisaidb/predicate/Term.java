package nihongo.chiisaidb.predicate;

public class Term {
	public static enum Operator {
		EQUAL, NOTEQUAL, GREATER, LESS;
	}

	private Expression lhs;
	private Expression rhs;
	private Operator op;

	public Term(Expression lhs, Expression rhs, Operator op) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.op = op;
	}
}
