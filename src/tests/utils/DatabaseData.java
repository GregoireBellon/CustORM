package tests.utils;

import orm.utils.AbstractDatabaseData;

public class DatabaseData extends AbstractDatabaseData {

	@Override
	public String getDatabasrUrl() {
		return "mysql://localhost:33333/TestSnake?user=User&password=Password";
	}

}
