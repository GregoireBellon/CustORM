package orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import orm.SQLFormatters.AbstractSQLFormatter;
import orm.SQLFormatters.MySQLFormatter;
import orm.annotations.Column;
import orm.annotations.Table;
import orm.exceptions.DaoObjectNotValidException;
import orm.selection.Comparator;
import orm.selection.Selector;
import tests.examples_entities.User;

public class ORM <T extends Entity>{

	private Class<T> type;

	private String table_name; 
	private List<DataField> fields;
	
	private AbstractSQLFormatter formatter;
	
	
	
	@SuppressWarnings("unchecked")
	public ORM(Class<? extends Entity> type, AbstractSQLFormatter formatter) throws DaoObjectNotValidException { 
		this.type = (Class<T>) type; 
		this.formatter = formatter;
		
		coherenceChecking(type);		
		
		this.table_name = type.getAnnotation(Table.class).name();
		
		this.fields = getFields(type);
	
	}

	
//	Récupère le premier champ ID
	public T getById(long num_id) throws NoSuchFieldException, SQLException {
		DataField id = fields.stream().filter(data -> data.getType() == DataTypes.ID).toList().get(0);
		
		if(id == null) {
			throw new NoSuchFieldException("There is no id field in this object " + type);
		}
		
		List<List<Selector>> selectors = new LinkedList<List<Selector>>();
		
		List<Selector> s = new ArrayList<Selector>();
		
		s.add(new Selector(id, Comparator.EQUALS, num_id));

		return this.getOne(selectors);
		
	}
	
	//	TODO : HANDLE SQL EXCEPTION
	public T getOne(List<List<Selector>> selectors) throws NoSuchFieldException, SQLException {

		try {
	
		List<DataField> fields = getFields(this.type);
		
		this.formatter.selectOne(table_name, fields, selectors);
		
		Constructor<T> empty_constructor = type.getConstructor(new Class<?>[]{});
		
		T obj = empty_constructor.newInstance(new Object[] {});
		
		populateFromFields(fields, obj);
		
		return obj;
			
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DaoObjectNotValidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public List<T> getAll() {
		return getAll(null, -1);
	}
	
	public List<T> getAll(List<List<Selector>> selectors) {	
		return getAll(selectors, -1);
	}
	
	public List<T> getAll(int limit){
		return getAll(null, limit);
	}
	
	
	public List<T> getAll(List<List<Selector>> selectors, int limit) {
		
		List<T> ret = new ArrayList<T>();
		
		try {
			
		List<DataField> object_fields = getFields(this.type);
		
		List<List<DataField>> query_result =  this.formatter.selectMultiple(table_name, object_fields, selectors, limit);
		
		for(List<DataField> single_result : query_result) {
			
			Constructor<T> empty_constructor = type.getConstructor(new Class<?>[]{});
			
			T obj = empty_constructor.newInstance(new Object[] {});
			
			populateFromFields(single_result, obj);
			
			ret.add(obj);
					
		}
		
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DaoObjectNotValidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}

			
	public void create(T inserted) {
		
		List<DataField> populated = this.populateToFields(this.fields, inserted);
		
		List<DataField> fields_excepted_id = populated.stream()
		  .filter(field -> 
			  {
				try {
					return !(field.getType() == DataTypes.ID) && field.getClass_field().get(inserted) != null;
				} catch (IllegalArgumentException | IllegalAccessException e) {
					System.err.println("ORM : Error while accessing to " + inserted.getClass().toString() + " object.\n"
							+ "Field : " + field.getClass_field().toString());
					return false;
				}
			}
		  ).collect(Collectors.toList());
		
		long id =formatter.insert(table_name, fields_excepted_id);
		
	}

	public void persist(T persisted) {
		
		List<DataField> populated = this.populateToFields(this.fields, persisted);

		List<List<Selector>> selector = new ArrayList<List<Selector>>();

		List<Selector> id_equality = new ArrayList<Selector>();
		
		populated.stream().filter(field -> field.getClass_field().getAnnotation(Column.class).datatype() == DataTypes.ID)
		.collect(Collectors.toList())
		.forEach(field -> id_equality.add(new Selector(field, Comparator.EQUALS, (Number) field.getValue())));;
		
		selector.add(id_equality);
		
		formatter.update(this.table_name, populated, selector);
		
	}
	
	private List<DataField> populateToFields(List<DataField> fields, T object){
		
		for(DataField field : fields) {
			try {
				
				System.out.println("field value : " + field.getClass_field().get(object));
				
				field.setValue(field.getClass_field().get(object));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				
				System.err.println("The object is not well formed");
				e.printStackTrace();
			
			}
		}
		
		return fields;
	}
	
	private void populateFromFields(List<DataField> fields, T object){
		
		for(DataField field : fields) {
			try {
				field.getClass_field().set(object, field.getValue());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				
				System.err.println("The object is not well formed");
				e.printStackTrace();
			
			}
		}
	}
	

	
	
	public static List<DataField> getFields(Class<? extends Entity> mon_entite) throws DaoObjectNotValidException {
				
		String table_name = mon_entite.getAnnotation(Table.class).name();
		
		List<DataField> fields = new ArrayList<DataField>();
		
		for(Field field : mon_entite.getFields()) {
			
			if(field.isAnnotationPresent(Column.class)) {
				Column a = field.getAnnotation(Column.class);
				fields.add(new DataField(a.datatype(), a.name(), field, table_name));
			}
			
		}
		
		return fields;

	}
	
	
	
	public static void coherenceChecking(Class<? extends Entity> mon_entite) throws DaoObjectNotValidException {
		
		if(!mon_entite.isAnnotationPresent(Table.class)) {
			throw new DaoObjectNotValidException("This object is not declared has a database table");
		}		
		
	}
	
	public static void createTable(Class<? extends Entity> mon_entite, AbstractSQLFormatter formatter) throws DaoObjectNotValidException {
		
		coherenceChecking(mon_entite);
		
		String table_name = mon_entite.getAnnotation(Table.class).name();
		
		List<DataField> fields =  ORM.getFields(mon_entite);
		
		formatter.createTable(table_name, fields);
	
		
	}
	
	public static void dropTable(Class<? extends Entity> mon_entite, AbstractSQLFormatter formatter) throws DaoObjectNotValidException {
		
		coherenceChecking(mon_entite);
		formatter.dropTable(mon_entite.getAnnotation(Table.class).name());
		
	}
	
}
