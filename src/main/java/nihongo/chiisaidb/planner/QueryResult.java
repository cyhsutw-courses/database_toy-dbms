package nihongo.chiisaidb.planner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nihongo.chiisaidb.type.Constant;

public class QueryResult {
	private Map<String, List<Constant>> columns;

	public QueryResult() {
		columns = new HashMap<String, List<Constant>>();
	}

	public void addColumns(String fname, List<Constant> values) {
		columns.put(fname, values);
	}

	public void print() {
		// TODO
	}
}
