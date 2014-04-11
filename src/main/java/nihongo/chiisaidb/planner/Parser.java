package nihongo.chiisaidb.planner;

import java.util.ArrayList;
import java.util.List;

import nihongo.chiisaidb.ErrorMessage;
import nihongo.chiisaidb.metadata.Schema;
import nihongo.chiisaidb.planner.data.CreateTableData;
import nihongo.chiisaidb.planner.data.InsertData;
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
		else
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
		return new CreateTableData(tblname, sch);
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
}
