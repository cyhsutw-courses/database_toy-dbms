import sqlparse

def parse(file_name):
	raw_sql_stmts = open(file_name, 'r').read(); 
	sqls = sqlparse.split(raw_sql_stmts)
	return sqls
