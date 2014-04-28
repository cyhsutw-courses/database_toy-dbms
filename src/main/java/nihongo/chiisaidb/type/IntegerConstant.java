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
		if (c instanceof VarcharConstant)
			throw new IllegalArgumentException();
		else if (c instanceof IntegerConstant)
			return val.compareTo((Integer) c.getValue());
		else
			throw new IllegalArgumentException();
	}

}
