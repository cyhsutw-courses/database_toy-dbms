package nihongo.chiisaidb.inmemory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.metadata.TableInfo;

public class InMemoryMgr {
	private Map<String, TableInMemoryScan> tablesInMemory = new HashMap<String, TableInMemoryScan>();

	public InMemoryMgr() {

	}

	public void loadAllTable() {
		Set<String> tns = Chiisai.mdMgr().getAllTableName();
		for (String s : tns) {
			Chiisai.imMgr().loadTableInMemory(s);
			Chiisai.imMgr().copyTableForSelfProduct(s);
		}
	}

	public void loadTableInMemory(String tblName) {
		TableInfo ti = Chiisai.mdMgr().getTableInfo(tblName);
		try {
			TableInMemoryScan tims = new TableInMemoryScan(tblName, ti);
			tablesInMemory.put(tblName, tims);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void copyTableForSelfProduct(String tblName) {
		TableInfo ti = Chiisai.mdMgr().getTableInfo(tblName);
		try {
			TableInMemoryScan tims = new TableInMemoryScan(tblName, ti);
			tablesInMemory.put(tblName + "_copy", tims);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TableInMemoryScan getTableInMemoryScan(String tblName) {
		return tablesInMemory.get(tblName);
	}
}
