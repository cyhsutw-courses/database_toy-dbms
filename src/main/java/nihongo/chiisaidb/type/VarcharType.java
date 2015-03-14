package nihongo.chiisaidb.type;

public class VarcharType extends Type {
	public static final VarcharType VARCHARTYPE = new VarcharType(0);

	private int maxlength;

	public VarcharType(int maxlength) {
		this.maxlength = maxlength;
	}

	public int maxLength() {
		return maxlength;
	}

	@Override
	public String toString() {
		return "Varchar";
	}

	@Override
	public int getDisplaySize() {
		return maxlength;
	}

	@Override
	public int numberOfBytes() {
		return maxlength+4;
	}
}
