package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import orm.DataField;
import orm.DataTypes;
import orm.Entity;
import orm.ORM;
import orm.ORMFactory;
import orm.exceptions.DaoObjectNotValidException;
import orm.selection.Comparator;
import orm.selection.Selector;
import orm.utils.AbstractDatabaseData;
import tests.examples_entities.User;
import tests.examples_entities.Vehicle;
import tests.utils.DatabaseData;

class TestCreate {
	
//	Fixture à l'arrache hopla

	public static AbstractDatabaseData setupDb() throws SQLException, DaoObjectNotValidException {
		
		AbstractDatabaseData data = new DatabaseData();
		data.setupDB(new Class[]{Vehicle.class, User.class}, true);
		
		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);
		
		User u = new User();
		u.name = "Aline";
		orm.create(u);
		
		User u2 = new User();
		u2.name = "Marc";
		orm.create(u2);
		
		return data;
	}
	
	
	@Test
	void getORM() throws SQLException, DaoObjectNotValidException {
		
		AbstractDatabaseData data = setupDb();
		
		
		System.out.println("getting ORM...");
		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);
	}


	@Test
	void insert() throws SQLException, DaoObjectNotValidException {

		AbstractDatabaseData data = setupDb();

		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);
		
		User u = new User();
		u.name = "Marc";
		orm.create(u);
		
	}
	
	@Test
	void getOne() throws SQLException, DaoObjectNotValidException, NoSuchFieldException, SecurityException{
		
		AbstractDatabaseData data = setupDb();

		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);
		
		List<List<Selector>> selectors = new LinkedList<List<Selector>>();
		
		List<Selector> s = new ArrayList<Selector>();
		
		s.add(new Selector(new DataField(User.class.getField("id")), Comparator.EQUALS, 1));
		
		selectors.add(s);
		
		User u = orm.getOne(selectors);
		
		System.out.println("User that we got : " + u.name);
	}
	
	@Test
	void getAll() throws SQLException, DaoObjectNotValidException, NoSuchFieldException, SecurityException {

		AbstractDatabaseData data = setupDb();

		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);
		
		List<User> users = orm.getAll();
						
		assert users.size() == 2;
		
	}
	
	@Test
	void getByID() throws SQLException, DaoObjectNotValidException, NoSuchFieldException, SecurityException{
		
		AbstractDatabaseData data = setupDb();

		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);
		
		User u = orm.getById(1);
		
		System.out.println("User that we got : " + u.name);
	}

	
	@Test
	void persist() throws SQLException, DaoObjectNotValidException, NoSuchFieldException {
		
		AbstractDatabaseData data = setupDb();

		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);

		User u = orm.getById(1);
		
		u.name = "Un autre nom";
		
		orm.persist(u);
		
		User autre = orm.getById(1);
		
		assert autre.name.equals(u.name);
				
	}
	
}
