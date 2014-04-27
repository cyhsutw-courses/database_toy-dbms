package nihongo.chiisaidb.predicate;

import nihongo.chiisaidb.storage.record.Record;
import nihongo.chiisaidb.type.Constant;
import nihongo.chiisaidb.type.IntegerConstant;

public class ConstantExpression implements Expression {
	private Constant val;

	/**
	 * Creates a new expression by wrapping a constant.
	 * 
	 * @param c
	 *            the constant
	 */
	public ConstantExpression(Constant c) {
		val = c;
	}

	/**
	 * Returns true.
	 * 
	 * @see Expression#isConstant()
	 */
	@Override
	public boolean isConstant() {
		return true;
	}

	/**
	 * Returns false.
	 * 
	 * @see Expression#isFieldName()
	 */
	@Override
	public boolean isFieldName() {
		return false;
	}

	/**
	 * Unwraps the constant and returns it.
	 * 
	 * @see Expression#asConstant()
	 */
	@Override
	public Constant asConstant() {
		return val;
	}

	/**
	 * This method should never be called. Throws a ClassCastException.
	 * 
	 * @see Expression#asFieldName()
	 */
	@Override
	public String asFieldName() {
		throw new ClassCastException();
	}

	/**
	 * Returns the constant, regardless of the record.
	 * 
	 * @see Expression#evaluate(Record)
	 */
	@Override
	public Constant evaluate(Record rec) {
		return val;
	}

	/**
	 * Return constant value as a string. If the constant type is varchar, the
	 * output string will be surrounded by single quotation marks.
	 */
	@Override
	public String toString() {
		if (val instanceof IntegerConstant)
			return val.getValue().toString();
		else
			return "'" + val.getValue().toString() + "'";

	}
}
