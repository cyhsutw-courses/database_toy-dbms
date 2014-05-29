package nigongo.chiisaidb.index;

import java.util.HashMap;
import java.util.Map;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.type.Type;

public class IndexMgr {
	private Map<IndexKey, Index> indexMap = new HashMap<IndexKey, Index>();

	public IndexMgr() {

	}

	public Index getIndex(IndexKey ik) {
		return indexMap.get(ik);
	}

	public void createIndex(IndexKey ik, IndexType ixtype) {
		String tblName = ik.getTblName();
		String fldName = ik.getFldName();
		Type fldType = Chiisai.mdMgr().getTableInfo(tblName).schema()
				.type(fldName);
		Index ix;
		if (ixtype.equals(IndexType.HashIndex))
			ix = new HashIndex(tblName, fldName, fldType);
		else if (ixtype.equals(IndexType.TreeIndex))
			ix = new TreeIndex(tblName, fldName, fldType);
		else
			throw new UnsupportedOperationException();
		try {
			ix.buildIndex();
			indexMap.put(ik, ix);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
