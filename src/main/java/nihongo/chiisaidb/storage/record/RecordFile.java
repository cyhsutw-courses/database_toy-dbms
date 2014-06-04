package nihongo.chiisaidb.storage.record;

import java.io.IOException;

import nihongo.chiisaidb.Chiisai;
import nihongo.chiisaidb.metadata.TableInfo;
import nihongo.chiisaidb.storage.FileMgr;
import nihongo.chiisaidb.type.Constant;
import nihongo.chiisaidb.type.Type;

public class RecordFile implements Record {

	private TableInfo ti;
	private String fileName;
	private FileMgr fileMgr = Chiisai.fMgr();
	private int recordId;
	private static int recordNum = -1; // number of reocrds

	public RecordFile(TableInfo ti) {
		this.ti = ti;
		this.fileName = ti.fileName();
		this.recordId = -1;
		// recordNum = -1;
	}

	public static void reset() {
		recordNum = -1;
	}

	public RecordFile(String fileName) {
		this.ti = null;
		this.fileName = fileName;
	}

	public void moveFilePointerToLast() throws IOException {
		// System.out.println("s--" + (numberOfRecords() * ti.recordSize() +
		// 4));
		recordNum++;
		fileMgr.moveFilePointer(fileName, (recordNum) * ti.recordSize() + 4);
		// fileMgr.moveFilePointerToLast(fileName);

	}

	public void beforeFirst() throws Exception {
		this.recordId = -1;
		// System.out.println("beforeFirst......QQ");
		fileMgr.beforeFirst(fileName);
	}

	public boolean next() throws Exception {
		this.recordId++;
		fileMgr.moveFilePointer(fileName, recordId * ti.recordSize() + 4);
		boolean hasNext = fileMgr.next(ti.fileName());
		return hasNext;
	}

	public int numberOfRecords() {
		try {
			return fileMgr.numberOfRecords(this.fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void updateNumberOfRecordsBy(int amount) {

		try {
			fileMgr.updateNumberOfRecordsBy(fileName, amount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Constant getVal(String fldName) throws IOException {
		int offset = this.ti.offset(fldName);
		offset += (this.recordId * ti.recordSize()) + 4;
		return fileMgr.getVal(this.fileName, offset,
				Type.typeInt(ti.schema().fields().get(fldName)));

	}

	@Override
	public Constant getVal(String fldName, String tblName) throws IOException {
		return getVal(fldName);
	}

	public void setVal(String fldName, Constant newVal) throws IOException {
		int offset = this.ti.offset(fldName);
		offset += (recordNum * ti.recordSize()) + 4;
		// System.out.println("recordNum: " + recordNum);
		fileMgr.setVal(fileName, offset, newVal);
	}

	public Constant getVal(int fldType) throws IOException {
		return fileMgr.getVal(fileName, fldType);
	}

	public void setVal(Constant newVal) throws IOException {
		fileMgr.setVal(fileName, newVal);
	}
}
