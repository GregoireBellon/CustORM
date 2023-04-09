package orm.SQLFormatters;

import java.math.BigInteger;
import java.sql.Connection;
import java.util.List;

import orm.DataField;
import orm.exceptions.NoResult;
import orm.selection.Selector;

public abstract class AbstractSQLFormatter {
	
	protected String db_url;
	
	public AbstractSQLFormatter(String db_url) {

		this.db_url = "jdbc:" + db_url;
	}

	public abstract BigInteger insert(String table_name, List<DataField> fields);
	
	public abstract void selectOne(String table_name, List<DataField> fields, List<List<Selector>> selectors) throws NoResult;
	
	public abstract List<List<DataField>> selectMultiple(String table_name, List<DataField> fields, List<List<Selector>> selectors ,int limit) throws NoResult;
	
	public abstract void update(String table_name, List<DataField> fields, List<List<Selector>> selectors);
		
	public abstract void delete(String table_name, List<List<Selector>> selectors);
	
	public abstract int createTable(String table_name, List<DataField> fields);
		
	public abstract int dropTable(String table_name);
	
	public abstract long count(String table_name, List<List<Selector>> selectors);
	
	public abstract Connection getRawSQLConnection();
		
	}
