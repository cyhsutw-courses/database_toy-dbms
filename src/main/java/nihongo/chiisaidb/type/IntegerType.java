package nihongo.chiisaidb.type;

public class IntegerType extends FieldType {
	public static final IntegerType INTEGERTYPE = new IntegerType();

	public IntegerType() {

	}

	@Override
	public String toString() {
		return "Integer";
	}
}
