package nihongo.chiisaidb.planner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.ErrorMessage;
import nihongo.chiisaidb.metadata.Schema;
import nihongo.chiisaidb.metadata.TableInfo;
import nihongo.chiisaidb.planner.data.CreateTableData;
import nihongo.chiisaidb.planner.data.InsertData;
import nihongo.chiisaidb.planner.data.QueryData;
import nihongo.chiisaidb.type.Constant;

public class Planner {
	private UpdatePlanner uplanner;
	private QueryPlanner qplanner;

	public Planner() {
		this.uplanner = new UpdatePlanner();
		this.qplanner = new QueryPlanner();
	}

	public void execute(String cmd) throws Exception {
		Parser parser = new Parser();
		Object ob = parser.updateCommand(cmd);
		if (ob instanceof CreateTableData) {
			Verifier.verifyCreateTableData((CreateTableData) ob);
			uplanner.executeCreateTable((CreateTableData) ob);
		} else if (ob instanceof InsertData) {
			List<Constant> values = parseInsertData((InsertData) ob);
			InsertData data = new InsertData(((InsertData) ob).tblName(),
					fieldNames((InsertData) ob), values);
			Verifier.verifyInsertData(data);
			uplanner.executeInsert(data);
		} else if (ob instanceof QueryData) {
			Verifier.verifyQueryData((QueryData) ob);
			qplanner.executeQuery((QueryData) ob);
		} else
			throw new UnsupportedOperationException();
	}

	public void showAll() throws Exception {
		uplanner.showAll();
	}

	private List<Constant> parseInsertData(InsertData data) {

		List<String> attriNames = fieldNames(data);

		Map<String, Constant> values = new HashMap<String, Constant>();

		List<String> fldNames = data.fieldNames();
		if (data.fieldNames().size() == 0) {
			fldNames = attriNames;
		}

		for (int i = 0; i < fldNames.size(); i++) {
			values.put(fldNames.get(i), data.values().get(i));
		}

		List<Constant> retVal = new ArrayList<Constant>();
		for (int i = 0; i < attriNames.size(); i++) {
			String fldName = attriNames.get(i);
			Constant value = values.get(fldName);
			retVal.add(value);
		}

		return retVal;

	}

	private List<String> fieldNames(InsertData data) {
		String tblName = data.tblName();
		if (!Chiisai.mdMgr().hasTable(tblName))
			throw new BadSemanticException(ErrorMessage.TABLE_NOT_EXIST);

		TableInfo tableInfo = Chiisai.mdMgr().getTableInfo(tblName);
		Schema schema = tableInfo.schema();

		return schema.fieldNames();
	}
}
