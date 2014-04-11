package nihongo.chiisaidb.planner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nihongo.chiisai.db.query.Predicate;

public class QueryData {
	// select
	boolean isAllField;
	List<String> fields;
	// from
	Set<String> tables;
	// where
	Predicate pred;

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
}
