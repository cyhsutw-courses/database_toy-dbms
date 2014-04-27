package nihongo.chiisaidb.planner;

import java.util.ArrayList;
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
		// System.out.println("***isAllField = " + data.isAllField());
		List<Integer> displaysize = new ArrayList<Integer>();
		boolean isOnlyOneTable = data.getTable2().isEmpty();

		TableInfo ti = Chiisai.mdMgr().getTableInfo(data.getTable1());
		Schema sch = ti.schema();
		TableInfo ti2 = null;
		Schema sch2 = null;
		// Deal with star
		if (data.isAllField()) {
			data.addField(sch.fieldNames());
			displaysize.addAll(sch.getDisplaySize());

			if (!isOnlyOneTable) {
				ti2 = Chiisai.mdMgr().getTableInfo(data.getTable2());
				sch2 = ti2.schema();
				data.addField(sch2.fieldNames());
				displaysize.addAll(sch2.getDisplaySize());
			}
		} else {
			if (isOnlyOneTable)
				for (int i = 0; i < data.fields().size(); i++) {
					int size = sch.getDisplaySize(data.fields().get(i));
					// System.out.print(size + ", ");
					displaysize.add(size);
				}
			else {
				ti2 = Chiisai.mdMgr().getTableInfo(data.getTable2());
				sch2 = ti2.schema();
				for (int i = 0; i < data.fields().size(); i++) {
					String fldName = data.fields().get(i);
					if (sch.hasField(fldName))
						displaysize.add(sch.getDisplaySize(fldName));
					else
						displaysize.add(sch2.getDisplaySize(fldName));
				}
			}
		}

		Scan s = new TableScan(data.getTable1());
		// Product
		if (!isOnlyOneTable)
			s = new ProductScan(s, new TableScan(data.getTable2()));

		// Select
		if (data.pred() != null)
			s = new SelectScan(s, data.pred());

		// Project
		if (!data.isAllField())
			s = new ProjectScan(s, data.fields());

		if (s instanceof TableScan)
			System.out.println("I'm TableScan~");
		else if (s instanceof ProductScan)
			System.out.println("I'm ProductScan~");
		else if (s instanceof SelectScan)
			System.out.println("I'm SelectScan~");
		else if (s instanceof ProjectScan)
			System.out.println("I'm ProjectScan~");
		else
			System.out.println("Who am I?");

		// Show Result
		showQueryResult(s, data.fields(), displaysize);

	}

	private void showQueryResult(Scan s, List<String> fields,
			List<Integer> displaysize) throws Exception {
		for (int i = 0; i < fields.size(); i++) {
			String fmt = "%" + (displaysize.get(i) + 2) + "s";
			System.out.format(fmt, fields.get(i));
		}
		System.out.println();
		s.beforeFirst();
		while (s.next()) {
			for (int i = 0; i < fields.size(); i++) {
				String fmt = "%" + (displaysize.get(i) + 2) + "s";
				System.out.format(fmt, s.getVal(fields.get(i)).getValue());
			}
			System.out.println();
		}
	}
}
