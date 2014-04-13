package nihongo.chiisaidb.type;

public class IntegerType extends Type {
	public static final IntegerType INTEGERTYPE = new IntegerType();
	private static final int INTEGER_DISPLAY_SIZE = 10;

	public IntegerType() {

	}

	@Override
	public String toString() {
		return "Integer";
	}

	@Override
	public int getDisplaySize() {
		return INTEGER_DISPLAY_SIZE;
	}
}
