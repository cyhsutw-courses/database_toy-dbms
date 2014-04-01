package nihongo.chiisaidb.metadata;

public class TableInfo {
	private String tblName;
	private Schema schema;

	public TableInfo(String tableName, Schema schema) {
		this.tblName = tableName;
		this.schema = schema;
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

	@Override
	public String toString() {
		return tblName + "\n" + schema.toString();
	}
}
