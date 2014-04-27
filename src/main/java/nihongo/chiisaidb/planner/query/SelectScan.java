package nihongo.chiisaidb.planner.query;

import nihongo.chiisaidb.predicate.Predicate;
import nihongo.chiisaidb.type.Constant;

public class SelectScan implements Scan {
	private Scan s;
	private Predicate pred;

	/**
	 * Creates a select scan having the specified underlying scan and predicate.
	 * 
	 * @param s
	 *            the scan of the underlying query
	 * @param pred
	 *            the selection predicate
	 */
	public SelectScan(Scan s, Predicate pred) {
		this.s = s;
		this.pred = pred;
	}

	// Scan methods

	@Override
	public void beforeFirst() throws Exception {
		s.beforeFirst();
	}

	/**
	 * Move to the next record satisfying the predicate. The method repeatedly
	 * calls next on the underlying scan until a suitable record is found, or
	 * the underlying scan contains no more records.
	 * 
	 * @see Scan#next()
	 */
	@Override
	public boolean next() throws Exception {
		// for test
		// System.out.println("SelectScan - next()");
		while (s.next()) {
			// System.out.println("Pred: " + pred.toString());
			boolean flag = pred.isSatisfied(s);
			// System.out.println("isStaisfied:" + flag);
			if (flag)
				return true;
		}
		return false;
	}

	@Override
	public Constant getVal(String fldName) throws Exception {
		return s.getVal(fldName);
	}

	@Override
	public boolean hasField(String fldName) throws Exception {
		return s.hasField(fldName);
	}
}
