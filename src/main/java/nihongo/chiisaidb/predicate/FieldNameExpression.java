package nihongo.chiisaidb.predicate;

import nihongo.chiisaidb.storage.record.Record;
import nihongo.chiisaidb.type.Constant;

public class FieldNameExpression implements Expression {
	private String fldName;

	/**
	 * Creates a new expression by wrapping a field.
	 * 
	 * @param fldName
	 *            the name of the wrapped field
	 */
	public FieldNameExpression(String fldName) {
		this.fldName = fldName;
	}

	/**
	 * Returns false.
	 * 
	 * @see Expression#isConstant()
	 */
	@Override
	public boolean isConstant() {
		return false;
	}

	/**
	 * Returns true.
	 * 
	 * @see Expression#isFieldName()
	 */
	@Override
	public boolean isFieldName() {
		return true;
	}

	/**
	 * This method should never be called. Throws a ClassCastException.
	 * 
	 * @see Expression#asConstant()
	 */
	@Override
	public Constant asConstant() {
		throw new ClassCastException();
	}

	/**
	 * Unwraps the field name and returns it.
	 * 
	 * @see Expression#asFieldName()
	 */
	@Override
	public String asFieldName() {
		return fldName;
	}

	/**
	 * Evaluates the field by getting its value from the record.
	 * 
	 * @throws Exception
	 * 
	 * @see Expression#evaluate(Record)
	 */
	@Override
	public Constant evaluate(Record rec) throws Exception {
		return rec.getVal(fldName);
	}

	@Override
	public String toString() {
		return fldName;
	}
}
