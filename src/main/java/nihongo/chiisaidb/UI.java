package nihongo.chiisaidb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nihongo.chiisaidb.metadata.Schema;
import nihongo.chiisaidb.storage.TableScan;
import nihongo.chiisaidb.type.Constant;
import nihongo.chiisaidb.type.IntegerConstant;
import nihongo.chiisaidb.type.IntegerType;
import nihongo.chiisaidb.type.VarcharConstant;
import nihongo.chiisaidb.type.VarcharType;

public class UI {

	public static void main(String[] args) throws Exception {
		UI ui = new UI();
		Chiisai.init();

		System.out.println("Chiisaidb start ... by nihongo");
		System.out.println("(input 'Quit' to terminate)\n");

		while (true) {
			String command = ui.promptCommand();
			if (command.compareToIgnoreCase("Quit") == 0) {
				try {
					ui.br.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				System.out.println("\nChiisaidb Quit ... ");
				break;
			}
			System.out.println("input:" + command);
			try {
				ui.testAPI(command);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}
	}

	private BufferedReader br;

	public UI() {
		this.br = new BufferedReader(new InputStreamReader(System.in));
	}

	private String promptCommand() {
		try {
			System.out.print("command: ");
			return br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void testAPI(String command) throws Exception {
		if (command.compareToIgnoreCase("Metadata") == 0)
			Chiisai.mdMgr().showMetadata();
		else if (command.compareToIgnoreCase("Showall") == 0)
			Chiisai.planner().showAll();
		else if (command.compareToIgnoreCase("Createtable") == 0) {
			String testTblName = "Student";
			Schema sch = new Schema();
			sch.addField("ID", IntegerType.INTEGERTYPE);
			sch.addField("Name", VarcharType.VARCHARTYPE);
			sch.addField("People Type", VarcharType.VARCHARTYPE);
			Chiisai.mdMgr().createTable(testTblName, sch);

			Chiisai.mdMgr().showMetadata();
		} else if (command.compareToIgnoreCase("Insert") == 0) {
			String testFileName = "Student.tbl";
			TableScan ts = new TableScan(testFileName);
			List<Constant> vals = new ArrayList<Constant>();
			vals.add(new IntegerConstant(9962231));
			vals.add(new VarcharConstant("Mr Chen"));
			vals.add(new VarcharConstant("OAQ"));
			ts.insert(vals);
			vals = new ArrayList<Constant>();
			vals.add(new IntegerConstant(9962210));
			vals.add(new VarcharConstant("CY Hsu"));
			vals.add(new VarcharConstant("O O"));
			ts.insert(vals);

			Chiisai.planner().showAll();
		} else
			Chiisai.planner().execute(command);
	}
}
