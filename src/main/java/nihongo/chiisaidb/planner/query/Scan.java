package nihongo.chiisaidb.planner.query;

import nihongo.chiisaidb.storage.record.Record;

public interface Scan extends Record {

	public void beforeFirst() throws Exception;

	public boolean next() throws Exception;

	public boolean hasField(String fldName) throws Exception;

	public void moveToRecordId(Integer i);

}
