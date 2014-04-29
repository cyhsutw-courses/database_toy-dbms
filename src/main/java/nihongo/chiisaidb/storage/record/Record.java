package nihongo.chiisaidb.storage.record;

import nihongo.chiisaidb.type.Constant;

public interface Record {

	public Constant getVal(String fldName) throws Exception;

	public Constant getVal(String fldName, String tblName) throws Exception;
}
