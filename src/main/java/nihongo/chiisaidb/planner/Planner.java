package nihongo.chiisaidb.planner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.ErrorMessage;
import nihongo.chiisaidb.metadata.Schema;
import nihongo.chiisaidb.metadata.TableInfo;
import nihongo.chiisaidb.type.Constant;
import nihongo.chiisaidb.type.Type;

public class Planner {
	private UpdatePlanner uplanner;

	public Planner() {
		this.uplanner = new UpdatePlanner();
	}

	public void executeUpdate(String cmd) throws Exception {
		Parser parser = new Parser();
		Object ob = parser.updateCommand(cmd);
		if (ob instanceof CreateTableData) {
			Verifier.verifierCreateTableData((CreateTableData) ob);
			uplanner.executeCreateTable((CreateTableData) ob);
		} else if (ob instanceof InsertData) {
			List<Constant> values = parseInsertData((InsertData) ob);
			InsertData data = new InsertData(((InsertData) ob).tblName(), fieldNames((InsertData) ob), values);
			Verifier.verifierInsertData(data);
			uplanner.executeInsert(data);
		} else
			throw new UnsupportedOperationException();
	}

	public void showAll() throws Exception {
		uplanner.showAll();
	}

	
	private List<Constant> parseInsertData(InsertData data){

		List<String> attriNames = fieldNames(data);

		Map<String, Constant> values = new HashMap<String, Constant>();
		
		List<String> fldNames = data.fieldNames();
		if(data.fieldNames().size()==0){
			fldNames = attriNames;
		}
		
		for(int i=0; i<fldNames.size(); i++){
			values.put(fldNames.get(i), data.values().get(i));
		}
		
		List<Constant> retVal = new ArrayList<Constant>();
		for(int i=0; i<attriNames.size(); i++){
			String fldName = attriNames.get(i);
			Constant value = values.get(fldName);
			retVal.add(value);
		}
		
		return retVal;
		
		
	}
	
	private List<String> fieldNames(InsertData data){
		String tblName = data.tblName();
		if (!Chiisai.mdMgr().hasTable(tblName))
			throw new BadSemanticException(ErrorMessage.TABLE_NOT_EXIST);

		TableInfo tableInfo = Chiisai.mdMgr().getTableInfo(tblName);
		Schema schema = tableInfo.schema();
		
		return schema.fieldNames();
	}
}
