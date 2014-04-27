package nihongo.chiisaidb.planner;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.planner.data.CreateTableData;
import nihongo.chiisaidb.planner.data.InsertData;
import nihongo.chiisaidb.storage.TableScan;

public class UpdatePlanner {

	public UpdatePlanner() {

	}

	public void executeCreateTable(CreateTableData data) throws Exception {
		Chiisai.mdMgr().createTable(data.tableName(), data.schema());
	}

	public void executeInsert(InsertData data) throws Exception {
		TableScan ts = new TableScan(data.tblName());
		ts.insert(data.values());
	}

	public void showAll() throws Exception {
		for (String tblName : Chiisai.mdMgr().getAllTableName()) {
			TableScan ts = new TableScan(tblName);
			System.out.println(tblName);
			ts.showAllRecord();
			System.out.println("");
		}
	}
}
