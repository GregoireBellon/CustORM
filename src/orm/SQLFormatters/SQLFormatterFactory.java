package orm.SQLFormatters;

import java.sql.SQLException;

import orm.utils.AbstractDatabaseData;

public class SQLFormatterFactory {
	
	
	public static AbstractSQLFormatter get(AbstractDatabaseData db_data) throws SQLException {
		
		String db_url = db_data.getDatabasrUrl();
		
		String sgbd = db_url.split(":")[0];
		
		if(sgbd.equals("mysql")) {
			return new MySQLFormatter(db_url);
		}
		
		return null;
		
	}
	
}
