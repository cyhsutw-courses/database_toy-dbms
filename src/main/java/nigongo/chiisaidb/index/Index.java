package nigongo.chiisaidb.index;

import java.util.List;

import nihongo.chiisaidb.type.Constant;

public interface Index {

	public void buildIndex() throws Exception;

	public List<Integer> getRecordIdList(Constant c);

	public void showIndex();
}
