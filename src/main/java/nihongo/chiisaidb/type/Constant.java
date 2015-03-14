package nihongo.chiisaidb.type;

public abstract class Constant implements Comparable<Constant> {

	public Constant() {

	}

	public abstract Object getValue();

	@Override
	public abstract int compareTo(Constant c);

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object o);
}
