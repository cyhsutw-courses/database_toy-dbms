package nihongo.chiisaidb.predicate;

import nihongo.chiisaidb.storage.record.RecordFile;

public class Term {
	public abstract static class Operator {
		abstract Operator complement();

		abstract boolean isSatisfied(Expression lhs, Expression rhs,
				RecordFile rec) throws Exception;
	}

	public static final Operator OP_EQ = new Operator() {
		@Override
		Operator complement() {
			return OP_NEQ;
		}

		@Override
		boolean isSatisfied(Expression lhs, Expression rhs, RecordFile rec)
				throws Exception {
			return lhs.evaluate(rec).equals(rhs.evaluate(rec));
		}

		@Override
		public String toString() {
			return "=";
		}
	};

	public static final Operator OP_NEQ = new Operator() {
		@Override
		Operator complement() {
			return OP_EQ;
		}

		@Override
		boolean isSatisfied(Expression lhs, Expression rhs, RecordFile rec)
				throws Exception {
			return !(lhs.evaluate(rec).equals(rhs.evaluate(rec)));
		}

		@Override
		public String toString() {
			return "<>";
		}
	};

	public static final Operator OP_LT = new Operator() {
		@Override
		Operator complement() {
			return OP_GT;
		}

		@Override
		boolean isSatisfied(Expression lhs, Expression rhs, RecordFile rec)
				throws Exception {
			return lhs.evaluate(rec).compareTo(rhs.evaluate(rec)) < 0;
		}

		@Override
		public String toString() {
			return "<";
		}
	};

	public static final Operator OP_GT = new Operator() {
		@Override
		Operator complement() {
			return OP_LT;
		}

		@Override
		boolean isSatisfied(Expression lhs, Expression rhs, RecordFile rec)
				throws Exception {
			return complement().isSatisfied(rhs, lhs, rec);
		}

		@Override
		public String toString() {
			return ">";
		}
	};

	private Expression lhs;
	private Expression rhs;
	private Operator op;

	public Term(Expression lhs, Expression rhs, Operator op) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.op = op;
	}

	public boolean isSatisfied(RecordFile rec) throws Exception {
		return op.isSatisfied(lhs, rhs, rec);
	}
}
