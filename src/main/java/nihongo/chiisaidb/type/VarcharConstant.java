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
}
