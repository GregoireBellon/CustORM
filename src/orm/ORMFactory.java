package orm;

import java.sql.SQLException;
import orm.SQLFormatters.AbstractSQLFormatter;
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
		
		return new ORM<T>(classe, formatter, data.getPrivateFields(classe));
	}
	
}
