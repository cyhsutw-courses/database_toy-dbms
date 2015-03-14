package nihongo.chiisaidb.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

public class SQLSpliter {

	private final PyObject parser;
	
	public SQLSpliter(){
		PythonInterpreter p = new PythonInterpreter(null ,new PySystemState()); 

	    PySystemState sys = Py.getSystemState();
	    sys.path.append(new PyString(System.getProperty("user.dir")));
	       
	    p.exec("from sql_parser import parse");
	       
	    this.parser = p.get("parse");
		
	}
	
	public List<String> splitSQLfromFile(String fileName){
		List<String> sqlStatements = new ArrayList<String>();
		
		PyObject pyRetVal = parser.__call__(new PyString(fileName));
        
	    Iterator<PyObject> sqlIterator = pyRetVal.asIterable().iterator();
	    while(sqlIterator.hasNext()){
	    	StringBuilder sqlBuilder = new StringBuilder(sqlIterator.next().asString());
	    	if(sqlBuilder.charAt(sqlBuilder.length()-1)!=';'){
	    		sqlBuilder.append(';');
	    	}
	    	sqlStatements.add(sqlBuilder.toString());
	    }
		
		return sqlStatements;
	}
	
	
}
