package nihongo.chiisaidb.storage.record;


import java.io.IOException;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.metadata.TableInfo;
import nihongo.chiisaidb.storage.FileMgr;
import nihongo.chiisaidb.type.Constant;
import nihongo.chiisaidb.type.Type;

public class RecordFile {

	private TableInfo ti;
	private String fileName;
	private FileMgr fileMgr = Chiisai.fMgr();
	
	public RecordFile(TableInfo ti){
		this.ti = ti;
		this.fileName = ti.fileName();
	}
	
	public RecordFile(String fileName)
	{
		this.ti = null;
		this.fileName = fileName;
	}
	
	public void beforeFirst() throws Exception{
		fileMgr.beforeFirst(fileName);
	}
	
	public boolean next() throws Exception{
		return Chiisai.fMgr().next(ti.fileName());		
	}
	
	public int numberOfRecords(){
		try {
			return fileMgr.numberOfRecords(this.fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void updateNumberOfRecordsBy(int amount)
	{
		try {
			fileMgr.updateNumberOfRecordsBy(fileName, amount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Constant getVal(int fldType) throws IOException
	{
		return fileMgr.getVal(fileName, fldType);
	}
	
	public Constant getVal(String fldName) throws IOException
	{
		return fileMgr.getVal(this.fileName, Type.typeInt(ti.schema().fields().get(fldName)));
		
	}
	
	public void setVal(Constant newVal) throws IOException
	{
		fileMgr.setVal(fileName, newVal);
	}
	
	public void setVal(String fldName, Constant newVal) throws IOException
	{
		fileMgr.setVal(this.fileName, newVal);
	}
}
