package nihongo.chiisaidb.planner.query.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.index.Index;
import nihongo.chiisaidb.index.IndexKey;
import nihongo.chiisaidb.index.TreeIndex;
import nihongo.chiisaidb.planner.query.Scan;
import nihongo.chiisaidb.predicate.Term;
import nihongo.chiisaidb.predicate.Term.Operator;
import nihongo.chiisaidb.type.Constant;

public class IndexSelectScan implements Scan {
	private Scan s;
	private List<Integer> recordIdList;
	private int li;

	public IndexSelectScan(Scan s, IndexKey ik, Constant targetValue,
			Operator op) {
		this.s = s;
		this.recordIdList = new ArrayList<Integer>();

		// build recordIdList
		Index ix = Chiisai.ixMgr().getIndex(ik);
		if (op == Term.OP_NEQ)
			throw new UnsupportedOperationException();
		else if (op == Term.OP_EQ)
			recordIdList.addAll(ix.getRecordIdList(targetValue));
		else if (op == Term.OP_LT) {
			if (!(ix instanceof TreeIndex))
				throw new UnsupportedOperationException();
			Set<Constant> constants = ((TreeIndex) ix).getValueToRecordId()
					.headMap(targetValue).keySet();
			for (Constant c : constants) {
				recordIdList.addAll(ix.getRecordIdList(c));
			}

		} else if (op == Term.OP_GT) {
			if (!(ix instanceof TreeIndex))
				throw new UnsupportedOperationException();
			Set<Constant> constants = ((TreeIndex) ix).getValueToRecordId()
					.tailMap(targetValue).keySet();
			for (Constant c : constants) {
				recordIdList.addAll(ix.getRecordIdList(c));
			}
		}

		//
		// System.out.print(ik.getTblName() + "(" + recordIdList.size() + ")");
		// for (Integer i : recordIdList) {
		// System.out.print(" " + i);
		// }
		// System.out.println();

	}

	@Override
	public void beforeFirst() throws Exception {
		li = -1;
		s.beforeFirst();
	}

	@Override
	public boolean next() throws Exception {
		li++;
		if (li < recordIdList.size()) {
			moveToRecordId(recordIdList.get(li));
			return true;
		} else
			return false;
	}

	@Override
	public void moveToRecordId(Integer i) {
		s.moveToRecordId(i);
	}

	@Override
	public Constant getVal(String fldName) throws Exception {
		return s.getVal(fldName);
	}

	@Override
	public Constant getVal(String fldName, String tblName) throws Exception {
		return s.getVal(fldName, tblName);
	}

	@Override
	public boolean hasField(String fldName) throws Exception {
		return s.hasField(fldName);
	}

}
