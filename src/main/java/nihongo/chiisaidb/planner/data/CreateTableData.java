package nihongo.chiisaidb.planner.data;

import nihongo.chiisaidb.metadata.Schema;

public class CreateTableData {
	private String tblName;
	private Schema schema;

	public CreateTableData(String tblName, Schema schema) {
		this.tblName = tblName;
		this.schema = schema;
	}

	public String tableName() {
		return tblName;
	}

	public Schema schema() {
		return schema;
	}

}
