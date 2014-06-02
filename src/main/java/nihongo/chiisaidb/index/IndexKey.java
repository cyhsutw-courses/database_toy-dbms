package nihongo.chiisaidb.index;


public class IndexKey {
	private String tblName;
	private String fldName;

	public IndexKey(String tblName, String fldName) {
		this.tblName = tblName;
		this.fldName = fldName;
	}

	@Override
	public int hashCode() {
		return (tblName + fldName).hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IndexKey))
			return false;
		IndexKey ik = (IndexKey) o;
		if (ik.getFldName().compareTo(fldName) == 0
				&& ik.getTblName().compareTo(tblName) == 0)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return tblName + "." + fldName;
	}

	public String getTblName() {
		return tblName;
	}

	public String getFldName() {
		return fldName;
	}
}
