package nihongo.chiisaidb.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nihongo.chiisaidb.type.Type;

public class Schema {
	private Map<String, Type> fields = new HashMap<String, Type>();
	private List<String> fieldNames = new ArrayList<String>();
	private Set<String> primaryKeySet = new HashSet<String>();

	public Schema() {

	}

	public void addField(String fieldname, Type type) {
		fields.put(fieldname, type);
		fieldNames.add(fieldname);
	}

	public Type type(String fieldname) {
		return fields.get(fieldname);
	}

	public List<String> fieldNames() {
		return fieldNames;
	}

	public Map<String, Type> fields() {
		return fields;
	}

	public void addAll(Schema schema) {
		this.fields.putAll(schema.fields());
		this.fieldNames.addAll(schema.fieldNames());
	}

	public void addPrimaryKey(String pkfield) {
		this.primaryKeySet.add(pkfield);
	}

	public void addPrimaryKey(int index) {
		this.primaryKeySet.add(fieldNames.get(index));
	}

	public Set<String> primaryKeySet() {
		return primaryKeySet;
	}

	public Set<Integer> getPrimaryKeyFieldIndexes() {
		Set<Integer> pkfis = new HashSet<Integer>();
		for (String pkf : primaryKeySet)
			pkfis.add(fieldNames.indexOf(pkf));
		return pkfis;
	}

	public boolean hasField(String fldName) {
		return fieldNames.contains(fldName);
	}

	public String format() {
		StringBuffer strbuffer = new StringBuffer();
		for (int i = 0; i < fieldNames.size(); i++) {
			String fieldName = fieldNames.get(i);
			strbuffer.append(fieldName);
			strbuffer.append(" ");
			strbuffer.append(fields.get(fieldName).toString());
			strbuffer.append(" ");
		}
		strbuffer.append("\n");
		return strbuffer.toString();
	}

	@Override
	public String toString() {
		StringBuffer strbuffer = new StringBuffer();
		int size = fieldNames.size();
		for (int i = 0; i < size; i++) {
			String fieldName = fieldNames.get(i);
			strbuffer.append(fieldName);
			strbuffer.append("[");
			strbuffer.append(fields.get(fieldName).toString());
			strbuffer.append("]");
			strbuffer.append(", ");
		}
		Iterator<String> iterator = primaryKeySet.iterator();
		strbuffer.append("#");
		while (iterator.hasNext()) {
			strbuffer.append(iterator.next());
		}
		strbuffer.append("\n");
		return strbuffer.toString();
	}
}
