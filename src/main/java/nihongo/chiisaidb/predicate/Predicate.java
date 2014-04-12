package nihongo.chiisaidb.predicate;

import nihongo.chiisaidb.storage.record.Record;

public class Predicate {
	public static enum Link {
		NONE, AND, OR;
	}

	private Term term1;
	private Term term2;
	private Link link;

	public Predicate(Term singleterm) {
		term1 = singleterm;
		term2 = null;
		link = Link.NONE;
	}

	public Predicate(Term term1, Term term2, Link link) {
		this.term1 = term1;
		this.term2 = term2;
		this.link = link;
	}

	public boolean isSatisfied(Record rec) throws Exception {
		if (!term1.isSatisfied(rec) || !term2.isSatisfied(rec))
			return false;
		return true;
	}

}
