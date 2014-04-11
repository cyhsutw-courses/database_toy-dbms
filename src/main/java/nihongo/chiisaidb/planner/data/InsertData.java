package nihongo.chiisaidb.planner.data;

import java.util.List;

import nihongo.chiisaidb.type.Constant;

public class InsertData {
	private String tblName;
	private List<String> fieldNames;
	private List<Constant> values;

	public InsertData(String tblName, List<String> fieldNames,
			List<Constant> values) {
		this.tblName = tblName;
		this.fieldNames = fieldNames;
		this.values = values;
	}

	public List<String> fieldNames() {
		return fieldNames;
	}

	public String tblName() {
		return tblName;
	}

	public String fileName() {
		return tblName + ".tbl";
	}

	
	public List<Constant> values() {
		return values;
	}

}
