package nihongo.chiisaidb.planner.query.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.index.Index;
import nihongo.chiisaidb.index.IndexKey;
import nihongo.chiisaidb.index.TreeIndex;
import nihongo.chiisaidb.inmemory.TableInMemoryScan;
import nihongo.chiisaidb.planner.query.Scan;
import nihongo.chiisaidb.predicate.Term;
import nihongo.chiisaidb.predicate.Term.Operator;
import nihongo.chiisaidb.type.Constant;

public class IndexSelectScan implements Scan {
	private TableInMemoryScan tims;
	private List<Integer> recordIdList;
	private int li;

	public IndexSelectScan(TableInMemoryScan tims, IndexKey ik,
			Constant targetValue, Operator op) {
		this.tims = tims;
		this.recordIdList = new ArrayList<Integer>();

		// build recordIdList
		Index ix = Chiisai.ixMgr().getIndex(ik);
		if (op == Term.OP_NEQ)
			throw new UnsupportedOperationException();
		else if (op == Term.OP_EQ)
			recordIdList.addAll(ix.getRecordIdList(targetValue));
		else if (op == Term.OP_GT) {
			if (!(ix instanceof TreeIndex))
				throw new UnsupportedOperationException();
			Set<Constant> s = ((TreeIndex) ix).getValueToRecordId()
					.headMap(targetValue).keySet();
			for (Constant c : s) {
				recordIdList.addAll(ix.getRecordIdList(c));
			}

		} else if (op == Term.OP_LT) {
			if (!(ix instanceof TreeIndex))
				throw new UnsupportedOperationException();
			Set<Constant> s = ((TreeIndex) ix).getValueToRecordId()
					.tailMap(targetValue).keySet();
			for (Constant c : s) {
				recordIdList.addAll(ix.getRecordIdList(c));
			}
		}

	}

	@Override
	public void beforeFirst() throws Exception {
		li = -1;
		tims.beforeFirst();
	}

	@Override
	public boolean next() throws Exception {
		li++;
		if (li < recordIdList.size()) {
			tims.moveToRecordId(recordIdList.get(li));
			return true;
		} else
			return false;
	}

	@Override
	public Constant getVal(String fldName) throws Exception {
		return tims.getVal(fldName);
	}

	@Override
	public Constant getVal(String fldName, String tblName) throws Exception {
		return tims.getVal(fldName, tblName);
	}

	@Override
	public boolean hasField(String fldName) throws Exception {
		return tims.hasField(fldName);
	}

}
