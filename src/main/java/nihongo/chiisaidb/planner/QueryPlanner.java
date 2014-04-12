package nihongo.chiisaidb.planner;

import java.util.List;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.metadata.Schema;
import nihongo.chiisaidb.metadata.TableInfo;
import nihongo.chiisaidb.planner.data.QueryData;
import nihongo.chiisaidb.planner.query.ProductScan;
import nihongo.chiisaidb.planner.query.ProjectScan;
import nihongo.chiisaidb.planner.query.Scan;
import nihongo.chiisaidb.planner.query.SelectScan;
import nihongo.chiisaidb.storage.TableScan;

public class QueryPlanner {

	public QueryPlanner() {

	}

	public void executeQuery(QueryData data) throws Exception {
		// Deal with star
		if (data.isAllField()) {
			TableInfo ti = Chiisai.mdMgr().getTableInfo(data.getTable1());
			Schema sch = ti.schema();
			data.addField(sch.fieldNames());

			if (!data.getTable2().isEmpty()) {
				TableInfo ti2 = Chiisai.mdMgr().getTableInfo(data.getTable2());
				Schema sch2 = ti2.schema();
				data.addField(sch2.fieldNames());
			}
		}

		Scan s = new TableScan(data.getTable1());
		// Product
		if (!data.getTable2().isEmpty())
			s = new ProductScan(s, new TableScan(data.getTable2()));

		// Select
		if (data.pred() != null)
			s = new SelectScan(s, data.pred());

		// Project
		if (!data.isAllField())
			s = new ProjectScan(s, data.fields());

		// Show Result
		showQueryResult(s, data.fields());
	}

	private void showQueryResult(Scan s, List<String> fields) throws Exception {

	}
}
