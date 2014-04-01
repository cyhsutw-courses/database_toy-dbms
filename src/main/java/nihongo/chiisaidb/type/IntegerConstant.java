package nihongo.chiisaidb.type;

public class IntegerConstant extends Constant {
	private Integer val;

	public IntegerConstant(Integer val) {
		this.val = val;
	}

	public IntegerConstant(int val) {
		this.val = val;
	}

	@Override
	public Integer getValue() {
		return val;
	}

}
