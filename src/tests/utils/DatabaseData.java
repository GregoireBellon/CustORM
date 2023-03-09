package tests.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orm.DescribeField;
import orm.Entity;
import orm.utils.AbstractDatabaseData;
import tests.examples_entities.User;
import tests.examples_entities.Vehicle;

public class DatabaseData extends AbstractDatabaseData {

	@Override
	public String getDatabasrUrl() {
		return "mysql://localhost:33333/TestSnake?user=User&password=Password";
	}

	@Override
	public Map<Class<? extends Entity>, List<DescribeField>> ListPrivateFields() {

		Map<Class<? extends Entity>, List<DescribeField>> map = new HashMap<Class<? extends Entity>, List<DescribeField>>();
		
		map.put(User.class, User.getPrivateFields());
		map.put(Vehicle.class, Vehicle.getPrivateFields());
		
		return map;
		
	}

}
