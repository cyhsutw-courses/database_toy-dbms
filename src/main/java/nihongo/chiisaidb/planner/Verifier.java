package nihongo.chiisaidb.planner;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.ErrorMessage;
import nihongo.chiisaidb.metadata.Schema;
import nihongo.chiisaidb.metadata.TableInfo;
import nihongo.chiisaidb.planner.data.CreateTableData;
import nihongo.chiisaidb.planner.data.InsertData;
import nihongo.chiisaidb.planner.data.QueryData;
import nihongo.chiisaidb.predicate.Expression;
import nihongo.chiisaidb.predicate.FieldNameExpression;
import nihongo.chiisaidb.predicate.Predicate;
import nihongo.chiisaidb.predicate.Term;
import nihongo.chiisaidb.storage.TableScan;
import nihongo.chiisaidb.type.Constant;
import nihongo.chiisaidb.type.IntegerConstant;
import nihongo.chiisaidb.type.IntegerType;
import nihongo.chiisaidb.type.Type;
import nihongo.chiisaidb.type.VarcharConstant;
import nihongo.chiisaidb.type.VarcharType;

public class Verifier {
	public static final int FIELD_NAME_MAXLENGTH = 40;

	private static String fldName;
	private static String attriName;
	private static Type type;
	private static Constant constant;
	private static TableInfo tableInfo1;
	private static TableInfo tableInfo2;
	private static TableScan tableScan;
	private static String primaryKey;
	private static boolean table2exist;

	public static void verifyCreateTableData(CreateTableData data) {

		// Check if table name is repeated
		String tblName = data.tableName();
		if (Chiisai.mdMgr().hasTable(tblName))
			throw new BadSemanticException(ErrorMessage.TABLE_REPEATED);

		Schema schema = data.schema();
		// Check if attribute name is repeated
		Set<String> set = new HashSet<String>(schema.fieldNames());
		if (set.size() != schema.fieldNames().size())
			throw new BadSemanticException(ErrorMessage.ATTRIBUTE_REPEATED);

		// Check if primaryKeySet > 1
		Set<String> primaryKeySet = schema.primaryKeySet();
		Iterator<String> iterator = primaryKeySet.iterator();
		if (iterator.hasNext()) {
			iterator.next();
			if (iterator.hasNext())
				throw new BadSemanticException(ErrorMessage.PRIMARYKEY_TOO_MANY);
		}

		Map<String, Type> fields = schema.fields();
		// Check if varchar(x) & x > FIELD_NAME_MAXLENGTH
		for (String fieldName : fields.keySet()) {
			type = fields.get(fieldName);
			if (type instanceof VarcharType)
				if (((VarcharType) type).maxLength() > FIELD_NAME_MAXLENGTH)
					throw new BadSemanticException(
							ErrorMessage.VARCHAR_EXCEED40);

		}

	}

	public static void verifyInsertData(InsertData data) throws Exception {
		// Check if table exist? (equal to table name is repeated)
		String tblName = data.tblName();
		if (!Chiisai.mdMgr().hasTable(tblName))
			throw new BadSemanticException(ErrorMessage.TABLE_NOT_EXIST);

		tableInfo1 = Chiisai.mdMgr().getTableInfo(tblName);
		Schema schema = tableInfo1.schema();
		Set<String> primaryKeySet = schema.primaryKeySet();

		Map<String, Type> fields = schema.fields();
		List<String> attriNames = schema.fieldNames();

		Iterator<String> iteratorAN = attriNames.iterator();

		List<String> fieldNames = data.fieldNames();
		if (fieldNames.size() == 0)
			fieldNames = attriNames;

		List<Constant> values = data.values();
		Iterator<String> iteratorFN = fieldNames.iterator();
		Iterator<Constant> iteratorValue = values.iterator();

		if (attriNames.size() != fieldNames.size())
			throw new BadSemanticException(
					ErrorMessage.WRONG_ATTRIBUTE_OR_ORDER);

		while (iteratorFN.hasNext()) {
			fldName = iteratorFN.next();
			attriName = iteratorAN.next();
			// Check if attributes do not exist
			if (attriName.equals(fldName))
				type = fields.get(fldName);
			else
				throw new BadSemanticException(
						ErrorMessage.WRONG_ATTRIBUTE_OR_ORDER);
			constant = iteratorValue.next();

			// Check if wrong value assigned to an attribute
			if (type instanceof VarcharType) {
				if (constant instanceof VarcharConstant) {
					// Check if varchar value longer than the given max
					// length
					if (((VarcharConstant) constant).getValue().length() > ((VarcharType) type)
							.maxLength()) {
						throw new BadSemanticException(
								ErrorMessage.INCORRECT_LENGTH);
					}
				} else
					throw new BadSemanticException(ErrorMessage.WRONG_VALUE);

			} else if (type instanceof IntegerType) {
				if (constant instanceof IntegerConstant) {
				} else
					throw new BadSemanticException(ErrorMessage.WRONG_VALUE);
			}

			// Check if the value of primaryKey is repeated?
			if (primaryKeySet.size() > 0) {
				primaryKey = primaryKeySet.iterator().next();
				if (fldName.equals(primaryKey)) {
					tableScan = new TableScan(tblName);
					if (tableScan.getPrimaryKeyValueSet().contains(
							constant.getValue()))
						throw new BadSemanticException(
								ErrorMessage.PRIMARYKEY_REPEATED);
				}
			}

		}

	}

	public static void verifyQueryData(QueryData data) {

		String tblName1 = data.getTable1();
		String tblName2 = data.getTable2();
		// check if table1, table2 not exist
		if (!Chiisai.mdMgr().hasTable(tblName1))
			throw new BadSemanticException(ErrorMessage.TABLE_NOT_EXIST);
		if (!tblName2.isEmpty()) {
			table2exist = true;
			if (Chiisai.mdMgr().hasTable(tblName2)) {
				tableInfo2 = Chiisai.mdMgr().getTableInfo(tblName1);
				//
			} else
				throw new BadSemanticException(ErrorMessage.TABLE_NOT_EXIST);
		} else
			table2exist = false;
		tableInfo1 = Chiisai.mdMgr().getTableInfo(tblName1);
		Schema schema1 = tableInfo1.schema();
		List<String> attriNames = schema1.fieldNames();

		// check if fldname not exist (select
		if (!data.isAllField()) {

			List<String> fieldNames = data.fields();
			List<String> prefixes = data.Prefix();

			if (fieldNames.size() == 0)
				throw new BadSemanticException(ErrorMessage.INCORRECT_FORMAT);

			Iterator<String> iteratorFN = fieldNames.iterator();
			Iterator<String> iteratorPR = prefixes.iterator();
			String prefix;
			if (!table2exist) {
				while (iteratorFN.hasNext()) {
					fldName = iteratorFN.next();
					prefix = iteratorPR.next();
					if (prefix.isEmpty()) {
						// set it to tblname1
					} else if (!data.getTable(prefix).equals(tblName1)) {
						throw new BadSemanticException(
								ErrorMessage.FIELD_NOT_EXIST);
					} else {
						// set it to tblname1
					}
					if (!attriNames.contains(fldName)) {
						throw new BadSemanticException(
								ErrorMessage.FIELD_NOT_EXIST);
					}
				}
			} else {
				tableInfo2 = Chiisai.mdMgr().getTableInfo(tblName2);
				Schema schema2 = tableInfo2.schema();
				List<String> attriNames2 = schema2.fieldNames();
				while (iteratorFN.hasNext()) {
					fldName = iteratorFN.next();
					prefix = iteratorPR.next();
					if (prefix.isEmpty()) {
						if (!attriNames.contains(fldName)) {
							// check table2
							// System.out.println(fldName + " not in " +
							// tblName1);
							if (!attriNames2.contains(fldName)) {
								// System.out.println(fldName + " not in " +
								// tblName2);
								throw new BadSemanticException(
										ErrorMessage.FIELD_NOT_EXIST);
							} else {
								// set it to tblname2
							}
						} else {
							if (!attriNames2.contains(fldName)) {
								// set it to tblname1

							} else {
								throw new BadSemanticException(
										ErrorMessage.FIELD_IN_BOTH_TABLE);
							}
						}
					} else {
						// not empty
					}
				}
			}
		}

		// check if fldname not exist (where

		// table name in expression could be nickname
		Predicate pred = data.pred();
		if (pred != null) {
			Term term1 = pred.getTerm1();
			Expression lhs = term1.getLHS();
			Expression rhs = term1.getRHS();
			if (!table2exist) {
				if (lhs instanceof FieldNameExpression)
					checkField((FieldNameExpression) lhs, data, attriNames,
							tblName1);
				if (rhs instanceof FieldNameExpression)
					checkField((FieldNameExpression) rhs, data, attriNames,
							tblName1);
				Term term2 = pred.getTerm2();
				if (term2 != null) {
					lhs = term2.getLHS();
					rhs = term2.getRHS();
					if (lhs instanceof FieldNameExpression)
						checkField((FieldNameExpression) lhs, data, attriNames,
								tblName1);
					if (rhs instanceof FieldNameExpression)
						checkField((FieldNameExpression) rhs, data, attriNames,
								tblName1);
				}
			} else {
				Schema schema2 = tableInfo2.schema();
				List<String> attriNames2 = schema2.fieldNames();
				if (lhs instanceof FieldNameExpression)
					checkField2((FieldNameExpression) lhs, data, attriNames,
							attriNames2, tblName1, tblName2);
				if (rhs instanceof FieldNameExpression)
					checkField2((FieldNameExpression) lhs, data, attriNames,
							attriNames2, tblName1, tblName2);

				Term term2 = pred.getTerm2();
				if (term2 != null) {
					lhs = term2.getLHS();
					rhs = term2.getRHS();
					if (lhs instanceof FieldNameExpression)
						checkField2((FieldNameExpression) lhs, data,
								attriNames, attriNames2, tblName1, tblName2);
					if (rhs instanceof FieldNameExpression)
						checkField2((FieldNameExpression) lhs, data,
								attriNames, attriNames2, tblName1, tblName2);
				}
			}
		}
	}

	private static void checkField(FieldNameExpression expression,
			QueryData data, List<String> attriNames, String tblName) {
		if (expression.asTableName().isEmpty())
			expression.setTableName(tblName);

		String tablename = data.getTable(expression.asTableName());
		if (tablename.isEmpty())
			throw new BadSemanticException(ErrorMessage.FIELD_NOT_EXIST);

		if (tablename.equals(tblName)
				&& attriNames.contains(expression.toString()))
			expression.setTableName(tblName);
		else
			throw new BadSemanticException(ErrorMessage.FIELD_NOT_EXIST);
	}

	private static void checkField2(FieldNameExpression expression,
			QueryData data, List<String> attriNames, List<String> attriNames2,
			String tblName1, String tblName2) {
		if (expression.asTableName().isEmpty()) {
			if (attriNames.contains(expression.toString()))
				if (attriNames2.contains(expression.toString()))
					throw new BadSemanticException(
							ErrorMessage.FIELD_IN_BOTH_TABLE);
				else
					expression.setTableName(tblName1);
			else if (attriNames2.contains(expression.toString()))
				expression.setTableName(tblName2);
			else
				throw new BadSemanticException(ErrorMessage.FIELD_NOT_EXIST);
		} else {
			String tablename = data.getTable(expression.asTableName());
			if (tablename.isEmpty())
				throw new BadSemanticException(ErrorMessage.FIELD_NOT_EXIST);

			if (tablename.equals(tblName1)
					&& attriNames.contains(expression.toString()))
				expression.setTableName(tblName1);
			else if (tablename.equals(tblName2)
					&& attriNames2.contains(expression.toString()))
				expression.setTableName(tblName2);
			else
				throw new BadSemanticException(ErrorMessage.FIELD_NOT_EXIST);
		}
	}
}
