package nihongo.chiisaidb;

import nigongo.chiisaidb.index.IndexMgr;
import nihongo.chiisaidb.inmemory.InMemoryMgr;
import nihongo.chiisaidb.metadata.MetadataMgr;
import nihongo.chiisaidb.planner.Planner;
import nihongo.chiisaidb.storage.FileMgr;

public class Chiisai {
	private static Planner planner;
	private static FileMgr fMgr;
	private static MetadataMgr mdMgr;
	private static InMemoryMgr imMgr;
	private static IndexMgr ixMgr;

	public Chiisai() {

	}

	public static void init() throws Exception {
		planner = new Planner();
		fMgr = new FileMgr();
		mdMgr = new MetadataMgr();
		imMgr = new InMemoryMgr();
		ixMgr = new IndexMgr();
	}

	public static Planner planner() {
		return planner;
	}

	public static MetadataMgr mdMgr() {
		return mdMgr;
	}

	public static FileMgr fMgr() {
		return fMgr;
	}

	public static InMemoryMgr imMgr() {
		return imMgr;
	}

	public static IndexMgr ixMgr() {
		return ixMgr;
	}

	public void showAll() throws Exception {
		planner.showAll();
	}

}
