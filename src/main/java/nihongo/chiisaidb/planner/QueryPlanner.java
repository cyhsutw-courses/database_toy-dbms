package nihongo.chiisaidb.planner;

import java.util.ArrayList;
import java.util.List;

import nihongo.chiisaidb.planner.data.QueryData;

public class QueryPlanner {

	public QueryPlanner() {

	}

	public QueryResult executeQuery(QueryData data) {
		QueryResult qr = new QueryResult();
		// Product
		if (data.tables().size() >= 2) {
			List<String> tl = new ArrayList<String>(data.tables());
			qr = productTableMathematical(tl.get(0), tl.get(1), qr);
		}
		// Select

		// Project

		return qr;
	}

	private QueryResult productTableMathematical(String tblName1,
			String tblName2, QueryResult qr) {
		return qr;
	}
}
