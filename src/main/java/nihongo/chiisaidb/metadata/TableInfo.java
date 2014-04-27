package nihongo.chiisaidb.metadata;

import java.util.HashMap;
import java.util.Map;

import nihongo.chiisaidb.type.Type;

public class TableInfo {
	private String tblName;
	private Schema schema;
	private Map<String, Integer> offsets = new HashMap<String, Integer>();
	private int recordSize;
	
	public TableInfo(String tableName, Schema schema) {
		this.tblName = tableName;
		this.schema = schema;
		
		int offset = 0;
		for(String fldName : schema.fieldNames()){
			Type fldType = schema.type(fldName);
			offsets.put(fldName, offset);
			offset += fldType.numberOfBytes();
		}
		this.recordSize = offset;
	}

	public String tableName() {
		return this.tblName;
	}

	public Schema schema() {
		return this.schema;
	}

	public String fileName() {
		return this.tblName + ".tbl";
	}

	public int offset(String fldName){
		return this.offsets.get(fldName);
	}
	
	public int recordSize(){
		return this.recordSize;
	}
	
	@Override
	public String toString() {
		return tblName + "\n" + schema.toString();
	}
}
