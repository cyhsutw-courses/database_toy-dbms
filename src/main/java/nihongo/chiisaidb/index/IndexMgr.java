package nihongo.chiisaidb.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.metadata.TableInfo;
import nihongo.chiisaidb.type.Type;

public class IndexMgr {
	private Map<IndexKey, Index> indexMap = new HashMap<IndexKey, Index>();

	public IndexMgr() {

	}

	public void createAllIndex() {
		Set<String> tns = Chiisai.mdMgr().getAllTableName();
		TableInfo ti;
		for (String tn : tns) {
			ti = Chiisai.mdMgr().getTableInfo(tn);
			List<String> l = ti.schema().fieldNames();
			for (String fn : l) {
				IndexKey ik = new IndexKey(tn, fn);
				createIndex(ik, IndexType.TreeIndex);
			}
		}
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
