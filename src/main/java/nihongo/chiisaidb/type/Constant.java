package nihongo.chiisaidb.type;

public abstract class Constant {

	public Constant() {

	}

	public abstract Object getValue();

	public abstract int compareTo(Constant c);

}
