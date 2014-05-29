package nihongo.chiisaidb.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nihongo.chiisaidb.metadata.Schema;
import nihongo.chiisaidb.metadata.TableInfo;
import nihongo.chiisaidb.planner.query.Scan;
import nihongo.chiisaidb.storage.TableScan;
import nihongo.chiisaidb.type.Constant;
import nihongo.chiisaidb.type.IntegerConstant;
import nihongo.chiisaidb.type.IntegerType;
import nihongo.chiisaidb.type.Type;
import nihongo.chiisaidb.type.VarcharConstant;
import nihongo.chiisaidb.type.VarcharType;

public class TableInMemoryScan implements Scan {
	private Map<String, List<String>> columnsMap;;
	private String tblName;
	private Schema sch;
	private int recordNum;
	private int ri;

	public TableInMemoryScan(String tblName, TableInfo ti) throws Exception {
		this.tblName = tblName;
		this.sch = ti.schema();
		this.recordNum = ti.recordSize() / 4 / sch.fieldNames().size();

		columnsMap = new HashMap<String, List<String>>();
		loadData();
	}

	@Override
	public void beforeFirst() {
		ri = -1;
		return;
	}

	@Override
	public boolean next() {
		ri++;
		if (ri < recordNum) {
			return true;
		} else
			return false;
	}

	@Override
	public boolean hasField(String fldName) {
		return sch.fieldNames().contains(fldName);
	}

	@Override
	public Constant getVal(String fldName) throws Exception {
		Type type = sch.type(fldName);
		List<String> column = columnsMap.get(fldName);
		if (type instanceof IntegerType) {
			Integer v = Integer.valueOf(column.get(ri));
			return new IntegerConstant(v);
		} else if (type instanceof VarcharType) {
			return new VarcharConstant(column.get(ri));
		} else
			throw new UnsupportedOperationException("wrong type for getvalue");
	}

	@Override
	public Constant getVal(String fldName, String tblName) throws Exception {
		if (tblName.isEmpty())
			return getVal(fldName);

		if (this.tblName.compareToIgnoreCase(tblName) != 0)
			throw new UnsupportedOperationException("wrong table for getvalue");

		return getVal(fldName);
	}

	public void moveToRecordId(Integer recordId) {
		ri = recordId.intValue();
	}

	public void showColumnsMap() {
		System.out.println("showColumnsMap ... " + columnsMap.size() + ", "
				+ recordNum);
		for (int i = 0; i < columnsMap.size(); i++) {
			String fldName = sch.fieldNames().get(i);
			System.out.println(fldName);
			List<String> columns = columnsMap.get(fldName);
			for (int j = 0; j < recordNum; j++) {
				System.out.print(columns.get(j) + " ");
			}
			System.out.println();
		}
	}

	private void loadData() throws Exception {
		TableScan ts = new TableScan(tblName);
		List<String> fldNames = sch.fieldNames();
		int fsize = fldNames.size();
		List<String>[] columns = new ArrayList[fsize];
		for (int i = 0; i < fsize; i++) {
			columns[i] = new ArrayList<String>();
		}

		ts.beforeFirst();
		while (ts.next()) {
			for (int i = 0; i < fldNames.size(); i++) {
				String v = ts.getVal(fldNames.get(i)).getValue().toString();
				System.out.println("value: " + v);
				columns[i].add(v);
			}
		}

		for (int i = 0; i < fldNames.size(); i++) {
			columnsMap.put(fldNames.get(i), columns[i]);
		}
	}
}
