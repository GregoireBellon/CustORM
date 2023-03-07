package orm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import orm.SQLFormatters.AbstractSQLFormatter;
import orm.SQLFormatters.MySQLFormatter;
import orm.SQLFormatters.SQLFormatterFactory;
import orm.exceptions.DaoObjectNotValidException;
import orm.utils.AbstractDatabaseData;

public class ORMFactory<T extends Entity>{

	
	private AbstractDatabaseData data;

	public ORMFactory(AbstractDatabaseData data) {
		this.data = data;
	}
	
	public ORM<T> getORM(Class<? extends Entity> classe) throws SQLException, DaoObjectNotValidException {

		AbstractSQLFormatter formatter = SQLFormatterFactory.get(data);
		
		return new ORM<T>(classe, formatter);
	}
	
}
