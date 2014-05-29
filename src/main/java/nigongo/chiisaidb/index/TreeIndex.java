package nigongo.chiisaidb.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import nihongo.chiisaidb.storage.TableScan;
import nihongo.chiisaidb.type.Constant;
import nihongo.chiisaidb.type.Type;

public class TreeIndex implements Index {
	private Map<Constant, List<Integer>> valueToRecordId = new TreeMap<Constant, List<Integer>>();
	private Type fldType;
	private String fldName;
	private String tblName;

	public TreeIndex(String tblName, String fldName, Type fldType) {
		this.fldType = fldType;
		this.fldName = fldName;
		this.tblName = tblName;
	}

	@Override
	public void buildIndex() throws Exception {
		TableScan ts = new TableScan(tblName);
		int ri = 0;
		ts.beforeFirst();
		while (ts.next()) {
			Constant c = ts.getVal(fldName);
			boolean isConstantInMap = valueToRecordId.containsKey(c);
			if (isConstantInMap) {
				List<Integer> l = valueToRecordId.get(c);
				l.add(ri);
			} else {
				List<Integer> l = new ArrayList<Integer>();
				l.add(ri);
				valueToRecordId.put(c, l);
			}
			ri++;
		}
	}

	@Override
	public List<Integer> getRecordIdList(Constant c) {
		return valueToRecordId.get(c);
	}

	@Override
	public void showIndex() {
		Set<Constant> keys = valueToRecordId.keySet();
		for (Constant c : keys) {
			System.out.print(c.getValue() + ": ");
			List<Integer> l = valueToRecordId.get(c);
			for (Integer i : l) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
	}

	public Type getFldType() {
		return fldType;
	}

	public String getFldName() {
		return fldName;
	}

	public String getTblName() {
		return tblName;
	}

}
