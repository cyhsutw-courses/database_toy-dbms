package nihongo.chiisaidb.planner;

import java.util.ArrayList;
import java.util.List;

import nihongo.chiisaidb.ErrorMessage;
import nihongo.chiisaidb.metadata.Schema;
import nihongo.chiisaidb.planner.data.CreateTableData;
import nihongo.chiisaidb.planner.data.InsertData;
import nihongo.chiisaidb.planner.data.QueryData;
import nihongo.chiisaidb.planner.query.Aggregation;
import nihongo.chiisaidb.predicate.ConstantExpression;
import nihongo.chiisaidb.predicate.Expression;
import nihongo.chiisaidb.predicate.FieldNameExpression;
import nihongo.chiisaidb.predicate.Predicate;
import nihongo.chiisaidb.predicate.Predicate.Link;
import nihongo.chiisaidb.predicate.Term;
import nihongo.chiisaidb.type.Constant;
import nihongo.chiisaidb.type.IntegerConstant;
import nihongo.chiisaidb.type.IntegerType;
import nihongo.chiisaidb.type.VarcharConstant;
import nihongo.chiisaidb.type.VarcharType;

public class Parser {

	private Lexer lex;

	// Need to deal with ; *****

	public Parser() {
	}

	private String id() {
		return lex.eatId();
	}

	private Constant constant() {
		if (lex.matchStringConstant())
			return new VarcharConstant(lex.eatStringConstant());
		else
			return new IntegerConstant(
					(new Double(lex.eatNumericConstant())).intValue());
	}

	private List<String> idList() {
		List<String> list = new ArrayList<String>();
		do {
			if (lex.matchDelim(','))
				lex.eatDelim(',');
			list.add(id());
		} while (lex.matchDelim(','));
		return list;
	}

	private List<Constant> constList() {
		List<Constant> list = new ArrayList<Constant>();
		do {
			if (lex.matchDelim(','))
				lex.eatDelim(',');
			list.add(constant());
		} while (lex.matchDelim(','));
		return list;
	}

	public Object updateCommand(String cmd) {
		lex = new Lexer(cmd);
		if (lex.matchKeyword("insert"))
			return insert();
		else if (lex.matchKeyword("create"))
			return create();
		else if (lex.matchKeyword("select")) {
			return select();
		} else
			throw new UnsupportedOperationException(ErrorMessage.SYNTAX_ERROR);

	}

	private InsertData insert() {
		lex.eatKeyword("insert");
		lex.eatKeyword("into");
		String tblname = lex.eatId();
		List<String> flds;
		List<Constant> vals;
		if (lex.matchKeyword("values")) {
			flds = new ArrayList<String>();
			lex.eatKeyword("values");
			lex.eatDelim('(');
			vals = constList();
			lex.eatDelim(')');
		} else {
			lex.eatDelim('(');
			// Need to preserve the order of ids
			flds = idList();
			lex.eatDelim(')');
			lex.eatKeyword("values");
			lex.eatDelim('(');
			vals = constList();
			lex.eatDelim(')');
		}
		if (!lex.matchDelim(';'))
			throw new UnsupportedOperationException(ErrorMessage.SYNTAX_ERROR);
		return new InsertData(tblname, flds, vals);
	}

	private Object create() {
		lex.eatKeyword("create");
		if (lex.matchKeyword("table"))
			return createTable();
		else
			throw new UnsupportedOperationException(ErrorMessage.SYNTAX_ERROR);
	}

	private CreateTableData createTable() {
		lex.eatKeyword("table");
		String tblname = lex.eatId();
		lex.eatDelim('(');
		Schema sch = fieldDefs();
		lex.eatDelim(')');
		if (!lex.matchDelim(';'))
			throw new UnsupportedOperationException(ErrorMessage.SYNTAX_ERROR);
		return new CreateTableData(tblname, sch);
	}

	private QueryData select() {
		QueryData querydata = new QueryData();
		lex.eatKeyword("select");
		if (lex.matchKeyword("count")) {
			lex.eatKeyword("count");
			querydata.setAggn(Aggregation.COUNT);
			lex.eatDelim('(');
			if (lex.matchDelim('*')) {
				lex.eatDelim('*');
				querydata.setIsAllField(true);
			} else {
				// Need To Handle Table.*
				querydata.setIsAllField(false);
				querydata.addField(id());
			}
			lex.eatDelim(')');

		} else if (lex.matchKeyword("sum")) {
			lex.eatKeyword("sum");
			querydata.setIsAllField(false);
			querydata.setAggn(Aggregation.SUM);
			lex.eatDelim('(');
			querydata.addField(id());
			lex.eatDelim(')');

		} else if (lex.matchDelim('*')) {
			lex.eatDelim('*');
			querydata.setIsAllField(true);

		} else {
			querydata.setIsAllField(false);
			selectField(querydata);
			while (lex.matchDelim(',')) {
				lex.eatDelim(',');
				selectField(querydata);
			}
		}

		if (lex.matchKeyword("from")) {
			lex.eatKeyword("from");
			String tblname1 = id();
			querydata.setTable(tblname1);
			querydata.setNickname1(tblname1);
			if (lex.matchKeyword("as")) {
				lex.eatKeyword("as");
				String nickname1 = id();
				querydata.setNickname1(nickname1);
			}
			if (lex.matchDelim(',')) {
				lex.eatDelim(',');
				String tblname2 = id();
				querydata.setTable(tblname1, tblname2);
				querydata.setNickname2(tblname2);
				if (lex.matchKeyword("as")) {
					lex.eatKeyword("as");
					String nickname2 = id();
					querydata.setNickname2(nickname2);
				}
				if (lex.matchKeyword("where")) {
					lex.eatKeyword("where");
					querydata.setPredicate(predicate());
				}
			} else if (lex.matchKeyword("where")) {
				lex.eatKeyword("where");
				querydata.setPredicate(predicate());
			} else if (lex.matchDelim(';')) {
			} else
				throw new UnsupportedOperationException(
						ErrorMessage.SYNTAX_ERROR);
		} else
			throw new UnsupportedOperationException(ErrorMessage.SYNTAX_ERROR);

		if (!lex.matchDelim(';'))
			throw new UnsupportedOperationException(ErrorMessage.SYNTAX_ERROR);

		return querydata;
	}

	private void selectField(QueryData querydata) {
		String name = id();
		if (lex.matchDelim('.')) {
			lex.eatDelim('.');
			if (lex.matchDelim('*')) {
				lex.eatDelim('*');
				querydata.addPrefix(name);
				querydata.addField("*");
			} else {
				querydata.addPrefix(name);
				querydata.addField(id());
			}
		} else {
			querydata.addPrefix("");
			querydata.addField(name);
		}
	}

	private Schema fieldDefs() {
		Schema schema = fieldDef();
		if (lex.matchKeyword("primary")) {
			lex.eatKeyword("primary");
			if (lex.matchKeyword("key")) {
				lex.eatKeyword("key");
				schema.addPrimaryKey(schema.fieldNames().get(0));
			} else
				throw new BadSyntaxException(ErrorMessage.SYNTAX_ERROR);
		}
		if (lex.matchDelim(',')) {
			lex.eatDelim(',');
			Schema schema2 = fieldDefs();
			schema.addAll(schema2);
		}

		return schema;
	}

	private Schema fieldDef() {
		String fldname = lex.eatId();
		return fieldType(fldname);
	}

	private Schema fieldType(String fldName) {
		Schema schema = new Schema();
		if (lex.matchKeyword("int")) {
			lex.eatKeyword("int");
			schema.addField(fldName, new IntegerType());
		} else {
			lex.eatKeyword("varchar");
			lex.eatDelim('(');
			double arg = lex.eatNumericConstant();
			lex.eatDelim(')');
			schema.addField(fldName, new VarcharType((int) arg));
		}
		return schema;
	}

	private Predicate predicate() {
		Predicate pred;
		Term term1 = term();
		if (lex.matchKeyword("and")) {
			lex.eatKeyword("and");
			Term term2 = term();
			pred = new Predicate(term1, term2, Link.AND);
		} else if (lex.matchKeyword("or")) {
			lex.eatKeyword("or");
			Term term2 = term();
			pred = new Predicate(term1, term2, Link.OR);
		} else {
			pred = new Predicate(term1);
		}
		return pred;
	}

	private Term term() {
		Expression lhs = queryExpression();
		Term.Operator op;
		if (lex.matchDelim('=')) {
			lex.eatDelim('=');
			op = Term.OP_EQ;
		} else if (lex.matchDelim('>')) {
			lex.eatDelim('>');
			op = Term.OP_GT;
		} else if (lex.matchDelim('<')) {
			lex.eatDelim('<');
			if (lex.matchDelim('>')) {
				lex.eatDelim('>');
				op = Term.OP_NEQ;
			} else {
				op = Term.OP_LT;
			}
		} else
			throw new BadSyntaxException(ErrorMessage.SYNTAX_ERROR);
		Expression rhs = queryExpression();
		return new Term(lhs, rhs, op);
	}

	private Expression queryExpression() {
		if (lex.matchId()) {
			String name = id();
			if (lex.matchDelim('.')) {
				lex.eatDelim('.');
				FieldNameExpression expression = new FieldNameExpression(id());
				expression.setTableName(name);
				return expression;
			} else
				return new FieldNameExpression(name);
		} else
			return new ConstantExpression(constant());
		// return lex.matchId() ? new FieldNameExpression(id())
		// : new ConstantExpression(constant());
	}

}
