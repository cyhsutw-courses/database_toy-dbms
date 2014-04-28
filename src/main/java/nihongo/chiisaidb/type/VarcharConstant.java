package nihongo.chiisaidb.type;

public class VarcharConstant extends Constant {
	private String val;

	public VarcharConstant(String val) {
		this.val = val;
	}

	@Override
	public String getValue() {
		return val;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		return compareTo((Constant) obj) == 0;
	}

	@Override
	public int compareTo(Constant c) {
		if (!(c instanceof VarcharConstant))
			throw new IllegalArgumentException();
		VarcharConstant sc = (VarcharConstant) c;
		return val.compareTo(sc.val);
	}
}
