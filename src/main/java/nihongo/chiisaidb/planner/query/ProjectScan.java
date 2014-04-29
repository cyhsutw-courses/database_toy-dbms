package nihongo.chiisaidb.planner.query;

import java.util.Collection;

import nihongo.chiisaidb.type.Constant;

public class ProjectScan implements Scan {
	private Scan s;
	private Collection<String> fieldList;

	/**
	 * Creates a project scan having the specified underlying scan and field
	 * list.
	 * 
	 * @param s
	 *            the underlying scan
	 * @param fieldList
	 *            the list of field names
	 */
	public ProjectScan(Scan s, Collection<String> fieldList) {
		this.s = s;
		this.fieldList = fieldList;
	}

	@Override
	public void beforeFirst() throws Exception {
		s.beforeFirst();
	}

	@Override
	public boolean next() throws Exception {
		return s.next();
	}

	@Override
	public Constant getVal(String fldName) throws Exception {
		if (hasField(fldName))
			return s.getVal(fldName);
		else
			throw new RuntimeException("field " + fldName + " not found.");
	}

	@Override
	public Constant getVal(String fldName, String tblName) throws Exception {
		if (hasField(fldName))
			return s.getVal(fldName, tblName);
		else
			throw new RuntimeException("field " + fldName + " not found.");
	}

	/**
	 * Returns true if the specified field is in the projection list.
	 * 
	 * @see Scan#hasField(java.lang.String)
	 */
	@Override
	public boolean hasField(String fldName) {
		return fieldList.contains(fldName);
	}
}
