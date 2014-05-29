package nihongo.chiisaidb.planner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.index.IndexKey;
import nihongo.chiisaidb.inmemory.TableInMemoryScan;
import nihongo.chiisaidb.metadata.Schema;
import nihongo.chiisaidb.metadata.TableInfo;
import nihongo.chiisaidb.planner.data.QueryData;
import nihongo.chiisaidb.planner.query.Aggregation;
import nihongo.chiisaidb.planner.query.ProductScan;
import nihongo.chiisaidb.planner.query.ProjectScan;
import nihongo.chiisaidb.planner.query.Scan;
import nihongo.chiisaidb.planner.query.SelectScan;
import nihongo.chiisaidb.planner.query.index.IndexSelectScan;
import nihongo.chiisaidb.predicate.Predicate;
import nihongo.chiisaidb.predicate.Predicate.Link;
import nihongo.chiisaidb.predicate.Term;
import nihongo.chiisaidb.type.Constant;
import nihongo.chiisaidb.type.IntegerConstant;

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

		// TableScan in memory
		Scan s = Chiisai.imMgr().getTableInMemoryScan(data.getTable1());

		// deal with Index
		Predicate pred = data.pred();
		if (isOnlyOneTable) {
			if (pred.getLink() == Link.NONE) {
				Term t = pred.getTerm1();
				if (t.isIndexWorked()) {
					IndexKey ik = new IndexKey(data.getTable1(),
							t.getIndexFieldName());
					s = new IndexSelectScan((TableInMemoryScan) s, ik,
							t.getIndexTargetValue(), t.getOp());
				}
			} else {
				// Link == AND or Link == OR
				Term t1 = pred.getTerm1();
				Term t2 = pred.getTerm2();
				if (t1.isIndexWorked()) {
					IndexKey ik = new IndexKey(data.getTable1(),
							t1.getIndexFieldName());
					s = new IndexSelectScan((TableInMemoryScan) s, ik,
							t1.getIndexTargetValue(), t1.getOp());
				}
				if (t2.isIndexWorked()) {
					IndexKey ik = new IndexKey(data.getTable1(),
							t2.getIndexFieldName());
					s = new IndexSelectScan((TableInMemoryScan) s, ik,
							t2.getIndexTargetValue(), t2.getOp());
				}
			}
		} else {
			// TableScan in memory for table2
			Scan s2 = Chiisai.imMgr().getTableInMemoryScan(data.getTable2());
			if (pred.getLink() == Link.NONE) {
				Term t = pred.getTerm1();
				String tblName = t.getIndexFieldTableName();
				if (t.isIndexWorked()) {
					IndexKey ik = new IndexKey(tblName, t.getIndexFieldName());
					s = new IndexSelectScan((TableInMemoryScan) s, ik,
							t.getIndexTargetValue(), t.getOp());
				}
			} else {
				// Link == AND or Link == OR
				Term t1 = pred.getTerm1();
				Term t2 = pred.getTerm2();
				if (t1.isIndexWorked()) {
					IndexKey ik = new IndexKey(t1.getIndexFieldTableName(),
							t1.getIndexFieldName());
					s = new IndexSelectScan((TableInMemoryScan) s, ik,
							t1.getIndexTargetValue(), t1.getOp());
				}
				if (t2.isIndexWorked()) {
					IndexKey ik = new IndexKey(t2.getIndexFieldTableName(),
							t2.getIndexFieldName());
					s = new IndexSelectScan((TableInMemoryScan) s, ik,
							t2.getIndexTargetValue(), t2.getOp());
				}
			}
			// Product
			s = new ProductScan(s, s2, data.getTable1(), data.getTable2());
		}

		// Select
		if (data.pred() != null)
			s = new SelectScan(s, data.pred());

		// Project
		if (!data.isAllField())
			s = new ProjectScan(s, data.fields());

		Set<String> fset = new HashSet<String>(data.fields());
		boolean isDupField = (fset.size() < data.fields().size());

		// Show Result
		if (data.getAggn() == Aggregation.COUNT)
			showCountResult(s, data.isAllField(), data.fields());
		else if (data.getAggn() == Aggregation.SUM)
			showSumResult(s, data.fields().get(0));
		else if (data.prefix().size() == 0)
			showQueryResult(s, data.fields(), displaysize);
		else if (isDupField)
			showQueryResultForDupField(s, data, displaysize);
		else
			showQueryResult(s, data.fields(), displaysize, data.prefix());

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

	private void showQueryResult(Scan s, List<String> fields,
			List<Integer> displaysize, List<String> prefix) throws Exception {
		for (int i = 0; i < fields.size(); i++) {
			String fmt = "%"
					+ (displaysize.get(i) + 2 + prefix.get(i).length()) + "s";
			if (!prefix.get(i).isEmpty())
				System.out.format(fmt, prefix.get(i) + "." + fields.get(i));
			else
				System.out.format(fmt, fields.get(i));
		}
		System.out.println();
		s.beforeFirst();
		while (s.next()) {
			for (int i = 0; i < fields.size(); i++) {
				String fmt = "%"
						+ (displaysize.get(i) + 2 + prefix.get(i).length())
						+ "s";
				System.out.format(fmt, s.getVal(fields.get(i)).getValue());
			}
			System.out.println();
		}
	}

	private void showQueryResultForDupField(Scan s, QueryData data,
			List<Integer> displaysize) throws Exception {
		List<String> fields = data.fields();
		List<String> prefix = data.prefix();
		Map<String, String> pftb = new HashMap<String, String>();

		pftb.put(data.getNickname1(), data.getTable1());
		pftb.put(data.getTable1(), data.getTable1());
		pftb.put(data.getNickname2(), data.getTable2());
		pftb.put(data.getTable2(), data.getTable2());

		for (int i = 0; i < fields.size(); i++) {
			String fmt = "%"
					+ (displaysize.get(i) + 2 + prefix.get(i).length()) + "s";
			if (!prefix.get(i).isEmpty())
				System.out.format(fmt, prefix.get(i) + "." + fields.get(i));
			else
				System.out.format(fmt, fields.get(i));
		}
		System.out.println();
		s.beforeFirst();
		while (s.next()) {
			for (int i = 0; i < fields.size(); i++) {
				String fmt = "%"
						+ (displaysize.get(i) + 2 + prefix.get(i).length())
						+ "s";
				System.out.format(fmt,
						s.getVal(fields.get(i), pftb.get(prefix.get(i)))
								.getValue());
			}
			System.out.println();
		}
	}

	private void showCountResult(Scan s, boolean isAllField, List<String> fields)
			throws Exception {
		// can't detect null
		// print field name
		System.out.print("COUNT(");
		if (isAllField)
			System.out.print("*");
		else
			for (int i = 0; i < fields.size(); i++) {
				System.out.print(fields.get(i));
				if (i != fields.size() - 1)
					System.out.print(", ");
			}
		System.out.println(")");

		s.beforeFirst();
		int count = 0;
		while (s.next()) {
			count++;
		}
		System.out.println("      " + count);
	}

	private void showSumResult(Scan s, String field) throws Exception {
		// can't detect null
		// print field name
		System.out.println("SUM(" + field + ")");

		s.beforeFirst();
		int total = 0;
		while (s.next()) {
			Constant c = s.getVal(field);
			if (c instanceof IntegerConstant)
				total += ((Integer) c.getValue()).intValue();
			else
				throw new UnsupportedOperationException();
		}
		System.out.println("    " + total);
	}
}
