package nihongo.chiisaidb.type;

import java.sql.Types;

public class Type {

	public static int typeInt(Type type) {
		if (type instanceof VarcharType)
			return Types.VARCHAR;
		else if (type instanceof IntegerType)
			return Types.INTEGER;
		else
			throw new UnsupportedOperationException();
	}

	public static Type getType(int typeInt) throws Exception {
		if (typeInt == Types.INTEGER)
			return new IntegerType();
		else if (typeInt == Types.VARCHAR)
			return new VarcharType(0);
		else 
			throw new Exception();
			
	}
	
	public static Type getType(int typeInt, int arg) throws Exception {
		if (typeInt == Types.INTEGER)
			return new IntegerType();
		else if (typeInt == Types.VARCHAR && arg>=0)
			return new VarcharType(arg);
		else
			throw new Exception();
	}
}
