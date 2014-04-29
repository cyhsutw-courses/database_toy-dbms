package nihongo.chiisaidb.planner.data;

import java.util.ArrayList;
import java.util.List;

import nihongo.chiisaidb.planner.query.Aggregation;
import nihongo.chiisaidb.predicate.Predicate;

public class QueryData {
	// select
	private boolean isAllField;
	private List<String> fields;
	// from
	private String table1;
	private String table2;
	private String nickname1;
	private String nickname2;

	// where
	private Predicate pred;
	// aggn
	private Aggregation aggn;

	public QueryData() {
		fields = new ArrayList<String>();
		this.pred = null;
		aggn = Aggregation.NONE;
	}

	public QueryData(boolean isAllField, Predicate pred) {
		this.isAllField = isAllField;
		fields = new ArrayList<String>();
		this.pred = pred;
		aggn = Aggregation.NONE;
	}

	public void addField(String fname) {
		if (isAllField)
			throw new UnsupportedOperationException(
					"haved been chosen all field.");
		fields.add(fname);
	}

	public void addField(List<String> fnames) {
		fields.addAll(fnames);
	}

	public void setTable(String tblName) {
		this.table1 = tblName;
		this.table2 = "";
	}

	public void setTable(String tblName1, String tblName2) {
		this.table1 = tblName1;
		this.table2 = tblName2;
	}

	public void setNickname1(String nickname) {
		this.nickname1 = nickname;
		this.nickname2 = "";
	}

	public void setNickname2(String nickname2) {
		this.nickname2 = nickname2;
	}

	public boolean isAllField() {
		return isAllField;
	}

	public List<String> fields() {
		return fields;
	}

	public String getTable1() {
		return table1;
	}

	public String getTable2() {
		return table2;
	}

	public String getNickname1() {
		return nickname1;
	}

	public String getNickname2() {
		return nickname2;
	}

	public Predicate pred() {
		return pred;
	}

	public void setPredicate(Predicate pred) {
		this.pred = pred;
	}

	public void setIsAllField(boolean isAllField) {
		this.isAllField = isAllField;
	}

	public void setAggn(Aggregation aggn) {
		this.aggn = aggn;
	}

	public Aggregation getAggn() {
		return aggn;
	}

}
