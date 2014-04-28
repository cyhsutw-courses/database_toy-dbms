package nihongo.chiisaidb.metadata;

import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.storage.FileMgr;
import nihongo.chiisaidb.storage.record.RecordFile;
import nihongo.chiisaidb.type.IntegerConstant;
import nihongo.chiisaidb.type.Type;
import nihongo.chiisaidb.type.VarcharConstant;
import nihongo.chiisaidb.type.VarcharType;

public class MetadataMgr {
	public static final String MD_FILE_NAME = "_metadata.cat";
	private Map<String, TableInfo> metadata;
	private static FileMgr fileMgr = Chiisai.fMgr();
	private static RecordFile MD_RF;
	
	public MetadataMgr() throws Exception {
		this.metadata = new HashMap<String, TableInfo>();
		if (Chiisai.fMgr().isNew())
			init();
		else{
			MD_RF = new RecordFile(MD_FILE_NAME);
			loadMetadata();
		}
	}

	public void showMetadata() {
		for (String tblName : metadata.keySet()) {
			System.out.println(metadata.get(tblName).toString());
		}
	}

	/**
	 * tblNum tblName1 fieldNum1 fieldName1 fieldType1 fieldName2 fieldType2 ...
	 * @throws Exception 
	 */
	private void loadMetadata() throws Exception {
		// read file in _metadata.cat
		int tblCount = MD_RF.numberOfRecords();
		MD_RF.beforeFirst();
	
		for(int i = 0; i<tblCount; i++){
			String tblName = (String) MD_RF.getVal(Types.VARCHAR).getValue();
			int fldNum = (Integer) MD_RF.getVal(Types.INTEGER).getValue();
			Schema sch = new Schema();
			for(int j=0; j<fldNum; j++){
				String fldName = (String) MD_RF.getVal(Types.VARCHAR).getValue();
				int fldType = (Integer) MD_RF.getVal(Types.INTEGER).getValue();
				Type fieldType;
				if(fldType==Types.INTEGER){
					fieldType = Type.getType(Types.INTEGER);
				}else{
					int varcharArg = (Integer) MD_RF.getVal(Types.INTEGER).getValue();
					fieldType = Type.getType(Types.VARCHAR, varcharArg);
				}
				sch.addField(fldName, fieldType);	
			}
			int pkNum = (Integer) MD_RF.getVal(Types.INTEGER).getValue();
			for (int j = 0; j < pkNum; j++) {
				int pkfi = (Integer) MD_RF.getVal(Types.INTEGER).getValue();
				sch.addPrimaryKey(pkfi);
			}
			TableInfo ti = new TableInfo(tblName, sch);
			metadata.put(tblName, ti);
		}

	}

	public void createTable(String tblName, Schema schema) throws Exception {
		
		MD_RF.setVal(new VarcharConstant(tblName));
		int fldNum = schema.fieldNames().size();
		MD_RF.setVal(new IntegerConstant(fldNum));
		for (int i = 0; i < fldNum; i++) {
			String fldName = schema.fieldNames().get(i);
			Type fldType = schema.type(fldName);
			MD_RF.setVal(new VarcharConstant(fldName));
			MD_RF.setVal(new IntegerConstant(Type.typeInt(fldType)));
			if(Type.typeInt(fldType)==Types.VARCHAR){
				MD_RF.setVal(new IntegerConstant(((VarcharType)fldType).maxLength()));
			}
		}
		Set<Integer> pkfis = schema.getPrimaryKeyFieldIndexes();
		MD_RF.setVal(new IntegerConstant(pkfis.size()));
		Iterator<Integer> iterator = pkfis.iterator();
		while (iterator.hasNext()) {
			MD_RF.setVal(new IntegerConstant(iterator.next()));
		}
		MD_RF.updateNumberOfRecordsBy(1);
		
		fileMgr.createFile(tblName + ".tbl");
		metadata.put(tblName, new TableInfo(tblName, schema));
		
	}

	public TableInfo getTableInfo(String tblName) {
		return metadata.get(tblName);
	}

	public Set<String> getAllTableName() {
		return metadata.keySet();
	}

	private void init() throws Exception {
		fileMgr.createFile(MD_FILE_NAME);
		MD_RF = new RecordFile(MD_FILE_NAME);
		MD_RF.updateNumberOfRecordsBy(0);
	}

	public boolean hasTable(String tblName) {
		return metadata.containsKey(tblName);
	}

}
