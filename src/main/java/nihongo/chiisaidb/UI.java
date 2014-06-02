package nihongo.chiisaidb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nihongo.chiisaidb.index.Index;
import nihongo.chiisaidb.index.IndexKey;
import nihongo.chiisaidb.index.IndexType;
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
		System.out.println("Chiisaidb init ...");
		Chiisai.init();

		System.out.print("loading tables to memory...");
		long l = System.currentTimeMillis();
		Chiisai.imMgr().loadAllTable();
		long spendms = System.currentTimeMillis() - l;
		System.out.println(spendms + " ms");

		System.out.print("creating index ...");
		l = System.currentTimeMillis();
		Chiisai.ixMgr().createAllIndex();
		spendms = System.currentTimeMillis() - l;
		System.out.println(spendms + " ms");

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
			String testTblName = "student";
			Schema sch = new Schema();
			sch.addField("id", new IntegerType());
			sch.addField("name", new VarcharType(7));
			sch.addField("peopletype", new VarcharType(5));
			Chiisai.mdMgr().createTable(testTblName, sch);

			String testTblName2 = "enroll";
			Schema sch2 = new Schema();
			sch2.addField("classname", new VarcharType(20));
			sch2.addField("id", new IntegerType());
			Chiisai.mdMgr().createTable(testTblName2, sch2);

			String testTblName3 = "professor";
			Schema sch3 = new Schema();
			sch3.addField("professorname", new VarcharType(20));
			sch3.addField("age", new IntegerType());
			Chiisai.mdMgr().createTable(testTblName3, sch3);

			Chiisai.mdMgr().showMetadata();
		} else if (command.compareToIgnoreCase("insert") == 0) {
			String testFileName = "student";
			TableScan ts = new TableScan(testFileName);
			List<Constant> vals = new ArrayList<Constant>();
			vals.add(new IntegerConstant(9962231));
			vals.add(new VarcharConstant("mr chen"));
			vals.add(new VarcharConstant("oaq"));
			ts.insert(vals);

			vals = new ArrayList<Constant>();
			vals.add(new IntegerConstant(9962210));
			vals.add(new VarcharConstant("cy hsu"));
			vals.add(new VarcharConstant("o o"));
			ts.insert(vals);

			String testFileName2 = "enroll";
			ts = new TableScan(testFileName2);
			vals = new ArrayList<Constant>();
			vals.add(new VarcharConstant("db"));
			vals.add(new IntegerConstant(9962231));
			ts.insert(vals);

			vals = new ArrayList<Constant>();
			vals.add(new VarcharConstant("ml"));
			vals.add(new IntegerConstant(9962231));
			ts.insert(vals);

			vals = new ArrayList<Constant>();
			vals.add(new VarcharConstant("db"));
			vals.add(new IntegerConstant(9962210));
			ts.insert(vals);

			String testFileName3 = "professor";
			ts = new TableScan(testFileName3);
			vals = new ArrayList<Constant>();
			vals.add(new VarcharConstant("cc wu"));
			vals.add(new IntegerConstant(34));
			ts.insert(vals);

			vals = new ArrayList<Constant>();
			vals.add(new VarcharConstant("hk hon"));
			vals.add(new IntegerConstant(38));
			ts.insert(vals);

			Chiisai.planner().showAll();
		} else if (command.compareToIgnoreCase("select1") == 0) {
			System.out.println("select name from student");
			QueryData data = new QueryData(false, null);
			data.setTable("student");
			data.addField("name");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("select2") == 0) {
			System.out.println("select name from student where id = 9962231");
			Term t = new Term(new FieldNameExpression("id"),
					new ConstantExpression(new IntegerConstant(9962231)),
					Term.OP_EQ);
			QueryData data = new QueryData(false, new Predicate(t));
			data.setTable("student");
			data.addField("name");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("select3") == 0) {
			System.out.println("select * from student");
			QueryData data = new QueryData(true, null);
			data.setTable("student");
			Chiisai.planner().testQuery(data);

		} else if (command.compareToIgnoreCase("select4") == 0) {
			System.out.println("select * from student where id = 9962231");
			Term t = new Term(new FieldNameExpression("id"),
					new ConstantExpression(new IntegerConstant(9962231)),
					Term.OP_EQ);
			QueryData data = new QueryData(true, new Predicate(t));
			data.setTable("student");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("select5") == 0) {
			System.out.println("select * from student, enroll");
			QueryData data = new QueryData(true, null);
			data.setTable("student", "enroll");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("select6") == 0) {
			System.out.println("select name from student, enroll");
			QueryData data = new QueryData(false, null);
			data.setTable("student", "enroll");
			data.addField("name");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("select7") == 0) {
			System.out
					.println("select * from student, enroll where id = 9962231");
			FieldNameExpression fne = new FieldNameExpression("id");
			fne.setTableName("student");
			Term t = new Term(fne, new ConstantExpression(new IntegerConstant(
					9962231)), Term.OP_EQ);
			QueryData data = new QueryData(true, new Predicate(t));
			data.setTable("student", "enroll");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("select8") == 0) {
			System.out.println("select * from student, enroll where id = id");
			FieldNameExpression f1 = new FieldNameExpression("id");
			f1.setTableName("student");
			FieldNameExpression f2 = new FieldNameExpression("id");
			f1.setTableName("enroll");
			Term t = new Term(f1, f2, Term.OP_EQ);
			QueryData data = new QueryData(true, new Predicate(t));
			data.setTable("student", "enroll");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("count3") == 0) {
			System.out.println("select count(*) from student");
			QueryData data = new QueryData(true, null);
			data.setTable("student");
			data.setAggn(Aggregation.COUNT);
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("count8") == 0) {
			System.out
					.println("select count(*) from student, enroll where id = id");
			FieldNameExpression f1 = new FieldNameExpression("id");
			f1.setTableName("student");
			FieldNameExpression f2 = new FieldNameExpression("id");
			f1.setTableName("enroll");
			Term t = new Term(f1, f2, Term.OP_EQ);
			QueryData data = new QueryData(true, new Predicate(t));
			data.setTable("student", "enroll");
			data.setAggn(Aggregation.COUNT);
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("sum3") == 0) {
			System.out.println("select sum(id) from student");
			QueryData data = new QueryData(false, null);
			data.setTable("student");
			data.addField("id");
			data.setAggn(Aggregation.SUM);
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("sum8") == 0) {
			System.out
					.println("select sum(id) from student, enroll where id = id");
			FieldNameExpression f1 = new FieldNameExpression("id");
			f1.setTableName("student");
			FieldNameExpression f2 = new FieldNameExpression("id");
			f1.setTableName("enroll");
			Term t = new Term(f1, f2, Term.OP_EQ);
			QueryData data = new QueryData(false, new Predicate(t));
			data.setTable("student", "enroll");
			data.setAggn(Aggregation.SUM);
			data.addField("id");
			Chiisai.planner().testQuery(data);
		} else if (command.compareToIgnoreCase("inMemory") == 0) {
			System.out.println("load　table");
			Chiisai.imMgr().loadTableInMemory("student");
			System.out.println("get TalbleInMemoryScan");
			TableInMemoryScan tims = Chiisai.imMgr().getTableInMemoryScan(
					"student");
			System.out.println("show columnsMap");
			tims.showColumnsMap();
		} else if (command.compareToIgnoreCase("buildIndex") == 0) {
			IndexKey ik = new IndexKey("student", "id");
			Chiisai.ixMgr().createIndex(ik, IndexType.HashIndex);
			Index ix = Chiisai.ixMgr().getIndex(ik);
			ix.showIndex();
		} else {
			long l = System.currentTimeMillis();
			Chiisai.planner().execute(command);
			long spendms = System.currentTimeMillis() - l;
			System.out.println(">>> total " + spendms + " ms");
		}
	}
}
