package nihongo.chiisaidb.planner.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nihongo.chiisaidb.predicate.Predicate;

public class QueryData {
	// select
	private boolean isAllField;
	private List<String> fields;
	// from
	private Set<String> tables;
	// where
	private Predicate pred;

	public QueryData(boolean isAllField, Predicate pred) {
		if (!isAllField)
			fields = new ArrayList<String>();
		else
			fields = null;

		this.pred = pred;
	}

	public void addField(String fname) {
		if (isAllField)
			throw new UnsupportedOperationException(
					"haved been chosen all field.");
		fields.add(fname);
	}

	public void addTable(String tblname) {
		tables.add(tblname);
	}

	public boolean isAllField() {
		return isAllField;
	}

	public List<String> fields() {
		return fields;
	}

	public Set<String> tables() {
		return tables;
	}

	public Predicate pred() {
		return pred;
	}
}
