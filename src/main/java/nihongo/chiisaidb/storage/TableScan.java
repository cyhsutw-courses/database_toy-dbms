package nihongo.chiisaidb.storage;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.metadata.TableInfo;
import nihongo.chiisaidb.planner.query.Scan;
import nihongo.chiisaidb.storage.record.RecordFile;
import nihongo.chiisaidb.type.Constant;

public class TableScan implements Scan {
	private TableInfo ti;
	private RecordFile rf;

	public TableScan(String tblName) {
		this.ti = Chiisai.mdMgr().getTableInfo(tblName);
		this.rf = new RecordFile(this.ti);
	}

	public void insert(List<Constant> values) throws Exception {

		rf.moveFilePointerToLast();

		int fieldNum = values.size();
		for (int i = 0; i < fieldNum; i++) {
			rf.setVal(ti.schema().fieldNames().get(i), values.get(i));
		}
		rf.updateNumberOfRecordsBy(1);

	}

	public Set<Object> getPrimaryKeyValueSet() throws Exception {
		Set<Object> pkvs = new HashSet<Object>();
		Set<Integer> pkfis = ti.schema().getPrimaryKeyFieldIndexes();

		rf.beforeFirst();

		Constant val;
		while (rf.next()) {
			for (int i = 0; i < ti.schema().fieldNames().size(); i++) {
				String fldName = ti.schema().fieldNames().get(i);
				val = rf.getVal(fldName);
				if (pkfis.contains(i)) {
					pkvs.add(val.getValue());
				}
			}
		}
		return pkvs;
	}

	@Override
	public void beforeFirst() throws Exception {
		rf.beforeFirst();
	}

	@Override
	public boolean next() throws Exception {
		return rf.next();
	}

	@Override
	public Constant getVal(String fldName) throws Exception {
		return rf.getVal(fldName);
	}

	@Override
	public Constant getVal(String fldName, String tblName) throws Exception {
		return rf.getVal(fldName);
	}

	@Override
	public boolean hasField(String fldName) throws Exception {
		return ti.schema().hasField(fldName);
	}

	public void showAllRecord() throws Exception {

		System.out.print(ti.schema().toString());
		System.out.println(rf.numberOfRecords() + " records");
		System.out.println();

		rf.beforeFirst();
		Constant val;
		while (rf.next()) {
			for (String fldName : ti.schema().fieldNames()) {
				val = rf.getVal(fldName);
				System.out.print(val.getValue() + ", ");
			}
			Iterator<String> iterator = ti.schema().primaryKeySet().iterator();
			System.out.print("#");
			while (iterator.hasNext()) {
				System.out.print("[" + iterator.next() + "] ");
			}
			System.out.println();

		}
	}

	@Override
	public void moveToRecordId(Integer i) {
		// Not implement
	}

}
