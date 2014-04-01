package nihongo.chiisaidb.planner;

import java.io.IOException;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.storage.TableScan;

public class UpdatePlanner {

	public UpdatePlanner() {

	}

	public void executeCreateTable(CreateTableData data) throws Exception {
		Chiisai.mdMgr().createTable(data.tableName(), data.schema());
	}

	public void executeInsert(InsertData data) throws IOException {
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
