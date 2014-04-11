package nihongo.chiisaidb.storage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import nihongo.chiisaidb.type.Constant;
import nihongo.chiisaidb.type.IntegerConstant;
import nihongo.chiisaidb.type.Type;
import nihongo.chiisaidb.type.VarcharConstant;

import java.sql.Types;

public class FileMgr {
	private File dir;
	private boolean isNew;
	private Map<String, FileChannel> openedFileChannels = new HashMap<String, FileChannel>();
	private Map<String, RandomAccessFile> openedFiles = new HashMap<String, RandomAccessFile>();
	static final String TableFileName = "ChiiSaiDB";
	static final int IntegerByteCount = 4;
	static final int DefaultNumberOfRecords = 0;
	
	public FileMgr() throws Exception{
		String homeDir = System.getProperty("user.home");
		dir = new File(homeDir, TableFileName);
		isNew = !dir.exists();
		if (isNew && !dir.mkdir())
			throw new Exception("cannot create " + TableFileName);
	}

	public boolean isNew() {
		return isNew;
	}
	
	public void createFile(String fileName) throws Exception{

		File dbTable = new File(dir, fileName);
		if (dbTable.exists()) {
			throw new IOException(fileName + " already exitsts!");
		} else {
			dbTable.createNewFile();
			appendInteger(fileName, 0);
		}
	}
	
	

	public void beforeFirst(String tableFileName) throws Exception{
		FileChannel fc = getFile(tableFileName);
		fc.position(IntegerByteCount);
	}
	
	public boolean next(String fileName) throws Exception{
		FileChannel fc = getFile(fileName);
		if(fc.size()==0)
			return false;
		else if(fc.position() - fc.size()==0)
			return false;
		else
			return true;
	}
	
	public Constant getVal(String fileName, int fldType) throws IOException
	{
		if(fldType == Types.INTEGER){
			return new IntegerConstant(readInteger(fileName));
		}else if(fldType == Types.VARCHAR){
			return new VarcharConstant(readString(fileName));
		}else{
			throw new UnsupportedOperationException();
		}
	}

	public void setVal(String fileName, Constant newVal) throws IOException
	{
		if(newVal instanceof VarcharConstant){
			appendString(fileName, (String) newVal.getValue());
		}else if(newVal instanceof IntegerConstant){
			appendInteger(fileName, (Integer) newVal.getValue());
		}else{
			throw new UnsupportedOperationException();
		}
	}
	
	public int numberOfRecords(String fileName) throws IOException
	{
		FileChannel fc = getFile(fileName);
		long currentPos = fc.position(); 
		int records = readInteger(fileName, 0);
		fc.position(currentPos);
		return records;
	}
	
	public void updateNumberOfRecordsBy(String fileName, int amount) throws IOException
	{
		FileChannel fc = getFile(fileName);
		long currentPos = fc.position(); 
		int records = readInteger(fileName, 0);
		writeInteger(fileName, records+amount, 0);
		fc.position(currentPos);
		
	}
	
	

	
	/************Private Methods**************/	
	
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
	
	/***** Read ByteBuffer 
	 * @throws IOException *****/
	private ByteBuffer readByteBuffer(String tableFileName, int position, int size) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(size);
		FileChannel fc = getFile(tableFileName);
		fc.read(bb, (long)position);
		return bb;
	}

	private ByteBuffer readByteBuffer(String tableFileName, int size) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(size);
		FileChannel fc = getFile(tableFileName);
		fc.read(bb);
		return bb;
	}

	/***** Read Integer 
	 * @throws IOException *****/
	private Integer readInteger(String tableFileName, int position) throws IOException {
		ByteBuffer bb = readByteBuffer(tableFileName, position, IntegerByteCount);		
		return bb.getInt(0);
	}

	private Integer readInteger(String tableFileName) throws IOException {
		ByteBuffer bb = readByteBuffer(tableFileName, IntegerByteCount);
		bb.rewind();
		return bb.getInt();
	}

	/***** Read String 
	 * @throws IOException *****/
	private String readString(String tableFileName) throws IOException {
		int length = readInteger(tableFileName);
		
		ByteBuffer bb = readByteBuffer(tableFileName, length);
		return new String(bb.array());
	}

	private String readString(String tableFileName, int position) throws IOException {
		
		int length = readInteger(tableFileName);
		ByteBuffer bb = readByteBuffer(tableFileName, position, length);
		bb.position(position);
		return new String(bb.array());
	}

	/***** Append 
	 * @throws IOException *****/
	private void append(String tableFileName, ByteBuffer bb) throws IOException {
		FileChannel fc = getFile(tableFileName);
		long position = fc.size();
		fc.write(bb, position);
		
	}
	
	private void appendString(String tableFileName, String stringToAppend) throws IOException {

		byte[] bytes = stringToAppend.getBytes();
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		appendInteger(tableFileName, bytes.length);
		append(tableFileName, bb);
	}

	private void appendInteger(String tableFileName, int integerToAppend) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(IntegerByteCount).putInt(integerToAppend);
		bb = ByteBuffer.wrap(bb.array());
		append(tableFileName, bb);
	}

	/***** Write Integer *****/
	private void writeInteger(String tableFileName, int integer, int position) {
		ByteBuffer bb = ByteBuffer.allocate(IntegerByteCount).putInt(integer);
		bb = ByteBuffer.wrap(bb.array());
		try {
			FileChannel fc = getFile(tableFileName);
			fc.write(bb, position);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
