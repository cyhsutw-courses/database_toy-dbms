package nihongo.chiisai.db.predicate;

import java.io.IOException;

import nihongo.chiisaidb.storage.record.RecordFile;
import nihongo.chiisaidb.type.Constant;

public interface Expression {
	/**
	 * Returns true if the expression is a constant.
	 * 
	 * @return true if the expression is a constant
	 */
	boolean isConstant();

	/**
	 * Returns true if the expression is a field reference.
	 * 
	 * @return true if the expression denotes a field
	 */
	boolean isFieldName();

	/**
	 * Returns the constant corresponding to a constant expression. Throws an
	 * exception if this expression does not denote a constant.
	 * 
	 * @return the expression as a constant
	 */
	Constant asConstant();

	/**
	 * Returns the field name corresponding to a field name expression. Throws
	 * an exception if this expression does not denote a field.
	 * 
	 * @return the expression as a field name
	 */
	String asFieldName();

	/**
	 * Evaluates the expression with respect to the specified record.
	 * 
	 * @param rec
	 *            the record
	 * @return the value of the expression, as a constant
	 * @throws IOException
	 */
	Constant evaluate(RecordFile rec) throws Exception;
}
