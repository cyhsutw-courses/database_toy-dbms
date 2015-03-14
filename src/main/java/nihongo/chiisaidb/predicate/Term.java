package nihongo.chiisaidb.predicate;

import nihongo.chiisaidb.storage.record.Record;
import nihongo.chiisaidb.type.Constant;

public class Term {
	public abstract static class Operator {
		abstract Operator complement();

		abstract boolean isSatisfied(Expression lhs, Expression rhs, Record rec)
				throws Exception;
	}

	public static final Operator OP_EQ = new Operator() {
		@Override
		Operator complement() {
			return OP_NEQ;
		}

		@Override
		boolean isSatisfied(Expression lhs, Expression rhs, Record rec)
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
		boolean isSatisfied(Expression lhs, Expression rhs, Record rec)
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
		boolean isSatisfied(Expression lhs, Expression rhs, Record rec)
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
		boolean isSatisfied(Expression lhs, Expression rhs, Record rec)
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

	public Expression getLHS() {
		return lhs;
	}

	public Expression getRHS() {
		return rhs;
	}

	public Operator getOp() {
		return op;
	}

	public boolean isIndexWorked() {
		if (op == Term.OP_NEQ)
			return false;
		if (lhs instanceof FieldNameExpression
				&& rhs instanceof FieldNameExpression)
			return false;
		return true;
	}

	public String getIndexFieldName() {
		if (lhs instanceof FieldNameExpression)
			return lhs.asFieldName();
		else if (rhs instanceof FieldNameExpression)
			return rhs.asFieldName();
		else
			return null;
	}

	public Constant getIndexTargetValue() {
		if (lhs instanceof ConstantExpression)
			return lhs.asConstant();
		else if (rhs instanceof ConstantExpression)
			return rhs.asConstant();
		else
			return null;
	}

	public String getIndexFieldTableName() {
		if (lhs instanceof FieldNameExpression)
			return ((FieldNameExpression) lhs).asTableName();
		else if (rhs instanceof FieldNameExpression)
			return ((FieldNameExpression) rhs).asTableName();
		else
			return null;
	}

	public boolean isSatisfied(Record rec) throws Exception {
		return op.isSatisfied(lhs, rhs, rec);
	}

	@Override
	public String toString() {
		return lhs.toString() + " " + op + " " + rhs.toString();
	}
}
