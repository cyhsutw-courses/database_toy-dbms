package nihongo.chiisaidb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nihongo.chiisaidb.inmemory.TableInMemoryScan;
import nihongo.chiisaidb.metadata.Schema;
import nihongo.chiisaidb.planner.data.QueryData;
import nihongo.chiisaidb.planner.query.Aggregation;
import nihongo.chiisaidb.predicate.ConstantExpression;
import nihongo.chiisaidb.predicate.FieldNameExpression;
import nihongo.chiisaidb.predicate.Predicate;
import nihongo.chiisaidb.predicate.Term;
import nihongo.chiisaidb.storage.TableScan;
import nihongo.chiisaidb.type.Constant;
import nihongo.chiisaidb.type.IntegerConstant;
import nihongo.chiisaidb.type.IntegerType;
import nihongo.chiisaidb.type.VarcharConstant;
import nihongo.chiisaidb.type.VarcharType;

public class UI {

	public static void main(String[] args) throws Exception {

		/*
		 * Added to show how to parse SQLs from a file
		 */
		/*
		 * SQLSpliter spliter = new SQLSpliter(); List<String> sqls =
		 * spliter.splitSQLfromFile("SQL_FILE.sql"); for(String sql : sqls){
		 * System.out.println(sql); }
		 */

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
		else if (command.compareToIgnoreCase("createtable") == 0) {
			String testTblName = "Student";
			Schema sch = new Schema();
			sch.addField("ID", new IntegerType());
			sch.addField("Name", new VarcharType(7));
			sch.addField("PeopleType", new VarcharType(5));
			Chiisai.mdMgr().createTable(testTblName, sch);

			String testTblName2 = "Enroll";
			Schema sch2 = new Schema();
			sch2.addField("ClassName", new VarcharType(20));
			sch2.addField("ID", new IntegerType());
			Chiisai.mdMgr().createTable(testTblName2, sch2);

			Chiisai.mdMgr().showMetadata();
		} else if (command.compareToIgnoreCase("insert") == 0) {
			String testFileName = "Student";
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

			String testFileName2 = "Enroll";
			ts = new TableScan(testFileName2);
			vals = new ArrayList<Constant>();
			vals.add(new VarcharConstant("DB"));
			vals.add(new IntegerConstant(9962231));
			ts.insert(vals);

			vals = new ArrayList<Constant>();
			vals.add(new VarcharConstant("ML"));
			vals.add(new IntegerConstant(9962231));
			ts.insert(vals);

			vals = new ArrayList<Constant>();
			vals.add(new VarcharConstant("DB"));
			vals.add(new IntegerConstant(9962210));
			ts.insert(vals);

			Chiisai.planner().showAll();
		} else if (command.compareToIgnoreCase("select1") == 0) {
			System.out.println("select name from student");
			QueryData data = new QueryData(false, null);
			data.setTable("Student");
			data.addField("Name");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("select2") == 0) {
			System.out.println("select name from student where id = 9962231");
			Term t = new Term(new FieldNameExpression("ID"),
					new ConstantExpression(new IntegerConstant(9962231)),
					Term.OP_EQ);
			QueryData data = new QueryData(false, new Predicate(t));
			data.setTable("Student");
			data.addField("Name");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("select3") == 0) {
			System.out.println("select * from student");
			QueryData data = new QueryData(true, null);
			data.setTable("Student");
			Chiisai.planner().testQuery(data);

		} else if (command.compareToIgnoreCase("select4") == 0) {
			System.out.println("select * from student where id = 9962231");
			Term t = new Term(new FieldNameExpression("ID"),
					new ConstantExpression(new IntegerConstant(9962231)),
					Term.OP_EQ);
			QueryData data = new QueryData(true, new Predicate(t));
			data.setTable("Student");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("select5") == 0) {
			System.out.println("select * from student, enroll");
			QueryData data = new QueryData(true, null);
			data.setTable("Student", "Enroll");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("select6") == 0) {
			System.out.println("select name from student, enroll");
			QueryData data = new QueryData(false, null);
			data.setTable("Student", "Enroll");
			data.addField("Name");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("select7") == 0) {
			System.out
					.println("select * from student, enroll where id = 9962231");
			Term t = new Term(new FieldNameExpression("ID"),
					new ConstantExpression(new IntegerConstant(9962231)),
					Term.OP_EQ);
			QueryData data = new QueryData(true, new Predicate(t));
			data.setTable("Student", "Enroll");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("select8") == 0) {
			System.out
					.println("select * from student, enroll where id = studentid");
			Term t = new Term(new FieldNameExpression("ID"),
					new FieldNameExpression("StudentId"), Term.OP_EQ);
			QueryData data = new QueryData(true, new Predicate(t));
			data.setTable("Student", "Enroll");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("count3") == 0) {
			System.out.println("select count(*) from student");
			QueryData data = new QueryData(true, null);
			data.setTable("Student");
			data.setAggn(Aggregation.COUNT);
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("count8") == 0) {
			System.out
					.println("select count(*) from student, enroll where id = studentid");
			Term t = new Term(new FieldNameExpression("ID"),
					new FieldNameExpression("StudentId"), Term.OP_EQ);
			QueryData data = new QueryData(true, new Predicate(t));
			data.setTable("Student", "Enroll");
			data.setAggn(Aggregation.COUNT);
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("sum3") == 0) {
			System.out.println("select sum(ID) from student");
			QueryData data = new QueryData(false, null);
			data.setTable("Student");
			data.addField("ID");
			data.setAggn(Aggregation.SUM);
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("sum8") == 0) {
			System.out
					.println("select sum(ID) from student, enroll where id = studentid");
			Term t = new Term(new FieldNameExpression("ID"),
					new FieldNameExpression("StudentId"), Term.OP_EQ);
			QueryData data = new QueryData(false, new Predicate(t));
			data.setTable("Student", "Enroll");
			data.setAggn(Aggregation.SUM);
			data.addField("ID");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("inMemory") == 0) {
			System.out.println("load　table");
			Chiisai.imMgr().loadTableInMemory("Student");
			System.out.println("get TalbleInMemoryScan");
			TableInMemoryScan tims = Chiisai.imMgr().getTableInMemoryScan(
					"Student");
			System.out.println("show columnsMap");
			tims.showColumnsMap();
		} else
			Chiisai.planner().execute(command);
	}
}
