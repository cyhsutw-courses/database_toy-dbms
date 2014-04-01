package nihongo.chiisaidb.planner;

public class Planner {
	private UpdatePlanner uplanner;

	public Planner() {
		this.uplanner = new UpdatePlanner();
	}

	public void executeUpdate(String cmd) throws Exception {
		Parser parser = new Parser();
		Object ob = parser.updateCommand(cmd);
		if (ob instanceof CreateTableData) {
			Verifier.verifierCreateTableData((CreateTableData) ob);
			uplanner.executeCreateTable((CreateTableData) ob);
		} else if (ob instanceof InsertData) {
			Verifier.verifierInsertData((InsertData) ob);
			uplanner.executeInsert((InsertData) ob);
		} else
			throw new UnsupportedOperationException();
	}

	public void showAll() throws Exception {
		uplanner.showAll();
	}

}
