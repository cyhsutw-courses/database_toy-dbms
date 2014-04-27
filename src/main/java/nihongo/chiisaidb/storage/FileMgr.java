package nihongo.chiisaidb.storage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import nihongo.chiisaidb.type.Constant;
import nihongo.chiisaidb.type.IntegerConstant;
import nihongo.chiisaidb.type.VarcharConstant;

public class FileMgr {
	private File dir;
	private boolean isNew;
	private Map<String, FileChannel> openedFileChannels = new HashMap<String, FileChannel>();
	private Map<String, RandomAccessFile> openedFiles = new HashMap<String, RandomAccessFile>();
	static final String TableFileName = "ChiiSaiDB";
	static final int IntegerByteCount = 4;
	static final int DefaultNumberOfRecords = 0;

	public FileMgr() throws Exception {
		String homeDir = System.getProperty("user.home");
		dir = new File(homeDir, TableFileName);
		isNew = !dir.exists();
		if (isNew && !dir.mkdir())
			throw new Exception("cannot create " + TableFileName);
	}

	public boolean isNew() {
		return isNew;
	}

	public void createFile(String fileName) throws Exception {

		File dbTable = new File(dir, fileName);
		if (dbTable.exists()) {
			throw new IOException(fileName + " already exitsts!");
		} else {
			dbTable.createNewFile();
			writeInteger(fileName, 0, 0);
		}
	}

	public void beforeFirst(String tableFileName) throws Exception {
		FileChannel fc = getFile(tableFileName);
		fc.position(IntegerByteCount);
	}

	public boolean next(String fileName) throws Exception {
		FileChannel fc = getFile(fileName);
		
		if (fc.size() == 0)
			return false;
		else if (fc.position() - fc.size() >= 0)
			return false;
		else
			return true;
	}

	public void moveFilePointer(String fileName, int offset) throws IOException{
		FileChannel fc = getFile(fileName);
		fc.position(offset);
	}
	
	public Constant getVal(String fileName, int position, int fldType) throws IOException {
		if (fldType == Types.INTEGER) {
			return new IntegerConstant(readInteger(fileName, position));
		} else if (fldType == Types.VARCHAR) {
			return new VarcharConstant(readString(fileName, position));
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public void setVal(String fileName, int position, Constant newVal) throws IOException {
		
		if (newVal instanceof VarcharConstant) {
			writeString(fileName, position, (String) newVal.getValue());
			
		} else if (newVal instanceof IntegerConstant) {
			
			writeInteger(fileName, position, (Integer) newVal.getValue());
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public Constant getVal(String fileName, int fldType) throws IOException{
		if (fldType == Types.INTEGER) {
			return new IntegerConstant(readInteger(fileName, -1));
		} else if (fldType == Types.VARCHAR) {
			return new VarcharConstant(readString(fileName, -1));
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public void setVal(String fileName, Constant newVal) throws IOException{
		if (newVal instanceof VarcharConstant) {
			writeString(fileName, -1, (String) newVal.getValue());
			
		} else if (newVal instanceof IntegerConstant) {
			
			writeInteger(fileName, -1, (Integer) newVal.getValue());
		} else {
			throw new UnsupportedOperationException();
		}
	}
	

	public int numberOfRecords(String fileName) throws IOException {
		FileChannel fc = getFile(fileName);
		long currentPos = fc.position();
		int records = readInteger(fileName, 0);
		fc.position(currentPos);
		return records;
	}

	public void updateNumberOfRecordsBy(String fileName, int amount)
			throws IOException {
		FileChannel fc = getFile(fileName);
		long currentPos = fc.position();
		int records = readInteger(fileName, 0);
		writeInteger(fileName, 0, records + amount);
		fc.position(currentPos);

	}

	/************ Private Methods **************/

	/***** Get FileChannel *****/
	private FileChannel getFile(String fileName) throws IOException {

		FileChannel fc = openedFileChannels.get(fileName);

		if (fc == null) {
			File dbTable = new File(dir, fileName);
			RandomAccessFile f = new RandomAccessFile(dbTable, "rws");

			fc = f.getChannel();
			openedFiles.put(fileName, f);
			openedFileChannels.put(fileName, fc);
		}
		return fc;
	}

	/*****
	 * Read ByteBuffer
	 * 
	 * @throws IOException
	 *****/
	private ByteBuffer readByteBuffer(String tableFileName, int position,
			int size) throws IOException {
		
		ByteBuffer bb = ByteBuffer.allocate(size);
		FileChannel fc = getFile(tableFileName);
		
		if(position!=-1){
			fc.read(bb, (long) position);
		}else{
			fc.read(bb);
		}
		
		return bb;
	}

	/*****
	 * Read Integer
	 * 
	 * @throws IOException
	 *****/
	private Integer readInteger(String tableFileName, int position)
			throws IOException {
		ByteBuffer bb = readByteBuffer(tableFileName, position,
				IntegerByteCount);
		bb.rewind();
		return bb.getInt();
	}
	/*****
	 * Read String
	 * 
	 * @throws IOException
	 *****/
	private String readString(String tableFileName, int position) throws IOException {
		int length = readInteger(tableFileName, position);
		
		int offset = position+4;
		if(position==-1){
			offset = -1;
		}
		
		ByteBuffer bb = readByteBuffer(tableFileName, offset, length);
		return new String(bb.array());
	}

	/*****
	 * write string
	 * 
	 * @throws IOException
	 *****/
	private void writeString(String tableFileName, int position, String stringToAppend) throws IOException{
		byte[] bytes = stringToAppend.getBytes();
		ByteBuffer bblen = ByteBuffer.allocate(IntegerByteCount).putInt(bytes.length);
		bblen = ByteBuffer.wrap(bblen.array());
		
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		FileChannel fc = getFile(tableFileName);
		
		if(position!=-1){
			fc.position(position);
		}
		fc.write(bblen);
		fc.write(bb);
	}

	/***** Write Integer 
	 * @throws IOException *****/
	private void writeInteger(String tableFileName, int position,  int integer) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(IntegerByteCount).putInt(integer);
		bb = ByteBuffer.wrap(bb.array());
		FileChannel fc = getFile(tableFileName);
		if(position!=-1){
			fc.position(position);
		}
		fc.write(bb);
	}

}
