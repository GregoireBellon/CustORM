package tests;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import orm.ORM;
import orm.ORMFactory;
import orm.exceptions.DaoObjectNotValidException;
import orm.selection.Comparator;
import orm.selection.Selector;
import orm.utils.AbstractDatabaseData;
import orm.utils.StringConversions;
import tests.examples_entities.User;
import tests.examples_entities.Vehicle;
import tests.utils.DatabaseData;

class TestORM {

	//	Fixture à l'arrache hopla

	@SuppressWarnings("unchecked")
	public static AbstractDatabaseData setupDb() throws SQLException, DaoObjectNotValidException {

		AbstractDatabaseData data = new DatabaseData();
		data.setupDB(new Class[]{Vehicle.class, User.class}, true);

		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);

		try {
		User u = new User();
		u.setName("Aline");
			u.hash = StringConversions.hashSHA256("mon_mot_de_passe");
		orm.create(u);

		User u2 = new User();
		u2.setName("Marc");
		u2.hash = StringConversions.hashSHA256("azerty");
		orm.create(u2);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}


	@Test
	void getORM() throws SQLException, DaoObjectNotValidException {

		AbstractDatabaseData data = setupDb();


		System.out.println("getting ORM...");


		@SuppressWarnings("unused")
		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);
	}


	@Test
	void insert() throws SQLException, DaoObjectNotValidException {

		AbstractDatabaseData data = setupDb();

		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);

		User u = new User();
		u.setName("Marc");
		orm.create(u);

	}

	@Test
	void getOne() throws SQLException, DaoObjectNotValidException, NoSuchFieldException, SecurityException{

		AbstractDatabaseData data = setupDb();

		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);

		List<List<Selector>> selectors = new LinkedList<List<Selector>>();

		List<Selector> s = new ArrayList<Selector>();

		s.add(new Selector( "id" , Comparator.EQUALS, 1));

		selectors.add(s);

		User u = orm.getOne(selectors);

		System.out.println("User that we got : " + u.getName());
	}

	@Test
	void getAll() throws SQLException, DaoObjectNotValidException, NoSuchFieldException, SecurityException {

		AbstractDatabaseData data = setupDb();

		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);

		List<User> users = orm.getAll();

		assert users.size() == 2;

	}

	void count() throws SQLException, DaoObjectNotValidException, NoSuchFieldException, SecurityException {

		AbstractDatabaseData data = setupDb();

		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);

		long users = orm.count();

		assert users == 2;
	}

	@Test
	void getByID() throws SQLException, DaoObjectNotValidException, NoSuchFieldException, SecurityException{

		AbstractDatabaseData data = setupDb();

		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);

		User u = orm.getById(BigInteger.valueOf(1));

		System.out.println("User that we got : " + u.getName());
	}


	@Test
	void persist() throws SQLException, DaoObjectNotValidException, NoSuchFieldException {

		AbstractDatabaseData data = setupDb();

		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);

		User u = orm.getById(BigInteger.valueOf(1));

		u.setName("Un autre nom");

		orm.persist(u);

		User autre = orm.getById(BigInteger.valueOf(1));

		assert autre.getName().equals(u.getName());

	}


	@Test
	void delete() throws SQLException, DaoObjectNotValidException, NoSuchFieldException {
		AbstractDatabaseData data = setupDb();

		ORM<User> orm = new ORMFactory<User>(data).getORM(User.class);
		
		User u = orm.getById(BigInteger.valueOf(1L));
		
		orm.delete(u);

		List<User> u_l = orm.getAll();

		assert u_l.size() == 1;

	}

}
