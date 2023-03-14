package orm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import orm.SQLFormatters.AbstractSQLFormatter;
import orm.annotations.Column;
import orm.annotations.Foreign;
import orm.annotations.Id;
import orm.annotations.NotNull;
import orm.annotations.Table;
import orm.exceptions.DaoObjectNotValidException;
import orm.selection.Comparator;
import orm.selection.Selector;
import orm.utils.StringConversions;

public class ORM <T extends Entity>{

	private Class<T> type;

	private String table_name; 
	private List<DataField> fields;

	private List<DescribeField> private_fields;
	
	private AbstractSQLFormatter formatter;



	public ORM(Class<? extends Entity> type, AbstractSQLFormatter formatter) throws DaoObjectNotValidException  { 

		this(type, formatter, new ArrayList<DescribeField>());
		
	}
	
	@SuppressWarnings("unchecked")
	public ORM(Class<? extends Entity> type, AbstractSQLFormatter formatter, List<DescribeField> private_fields)throws DaoObjectNotValidException{
		
		this.type = (Class<T>) type;
		
		this.formatter = formatter;

		coherenceChecking(type, private_fields);		

		this.table_name = type.getAnnotation(Table.class).name();
		
		this.private_fields = private_fields;
		
		this.fields = getFields(type, private_fields);		
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

	public Connection getRawSQLConnection() {
		return this.formatter.getRawSQLConnection();
	}
	
	//	TODO : HANDLE SQL EXCEPTION
	public T getOne(List<List<Selector>> selectors) throws NoSuchFieldException, SQLException {

		try {

			List<DataField> fields = getFields(this.type, this.private_fields);

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

			List<DataField> object_fields = getFields(this.type, this.private_fields);

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


	public long create(T inserted) {

		List<DataField> populated = this.populateToFields(this.fields, inserted);

		List<DataField> fields_excepted_id = populated.stream()
				.filter(field -> 
				{
					try {
						return !(field.getType() == DataTypes.ID) && field.getValue() != null;
					} catch (IllegalArgumentException e) {
						System.err.println("ORM : Error while accessing to " + inserted.getClass().toString() + " object.\n"
								+ "Field : " + field.getClass_field_name());
						return false;
					}
				}
						).collect(Collectors.toList());

		long id = formatter.insert(table_name, fields_excepted_id);

		return id;
	}

	public void persist(T persisted) {

		List<DataField> populated = this.populateToFields(this.fields, persisted);

		List<List<Selector>> selector = new ArrayList<List<Selector>>();

		List<Selector> id_equality = new ArrayList<Selector>();

		populated.stream().filter(field -> field.getType() == DataTypes.ID)
		.collect(Collectors.toList())
		.forEach(field -> id_equality.add(new Selector(field, Comparator.EQUALS, (Number) field.getValue())));;

		selector.add(id_equality);

		formatter.update(this.table_name, populated, selector);
	}
	
	public void delete(T deleted) {
		
		List<DataField> populated = this.populateToFields(this.fields, deleted);

		List<List<Selector>> selector = new ArrayList<List<Selector>>();

		List<Selector> id_equality = new ArrayList<Selector>();

		populated.stream().filter(field -> field.getType() == DataTypes.ID)
		.collect(Collectors.toList())
		.forEach(field -> id_equality.add(new Selector(field, Comparator.EQUALS, (Number) field.getValue())));;

		selector.add(id_equality);
		
		formatter.delete(this.table_name, selector);

				
	}

	private List<DataField> populateToFields(List<DataField> fields, T object){

		for(DataField field : fields) {

			try {

				field.setValue(type.getField(field.getClass_field_name()).get(object));

			} catch (IllegalArgumentException | IllegalAccessException  | SecurityException  e) {

				System.err.println("The object is not well formed");
				e.printStackTrace();

			} catch (NoSuchFieldException e) {
				try {
					Method getter = field.getEntity_class().getMethod(StringConversions.toLowerCamelCase("get_"+field.getClass_field_name()));

					field.setValue(getter.invoke(object));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException  e1) {
					//				Erreur check dans coherence exception
					e1.printStackTrace();
				}

			}
		}

		return fields;
	}

	private void populateFromFields(List<DataField> fields, T object){

		for(DataField field : fields) {
			try {
				type.getField(field.getClass_field_name()).set(object, field.getValue());

			}
			catch (IllegalArgumentException | IllegalAccessException  | SecurityException  e) {

				System.err.println("The object is not well formed");
				e.printStackTrace();

			} catch (NoSuchFieldException e) {
				try {
					Method setter = field.getEntity_class().getMethod(StringConversions.toLowerCamelCase("set_"+field.getClass_field_name()), field.getClass_field_type());
					setter.invoke(object, field.getValue());

				}catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException  e1) {
					//				Erreur check dans coherence exception
					e1.printStackTrace();
				}



			}
		}
	}




	public static List<DataField> getFields(Class<? extends Entity> mon_entite, List<DescribeField> private_fields) throws DaoObjectNotValidException {

		String table_name = mon_entite.getAnnotation(Table.class).name();

		List<DataField> fields = new ArrayList<DataField>();


		//		Récupération des fields publiques


		for(Field field : mon_entite.getFields()) {

			if(field.isAnnotationPresent(Column.class)) {

				DataField added_datafield = new DataField(field);

				if(field.isAnnotationPresent(Id.class)) {
					added_datafield.getConstraints().add(Constraint.ID);
				}
				if(field.isAnnotationPresent(NotNull.class)) {
					added_datafield.getConstraints().add(Constraint.NOT_NULL);
				}
				if(field.isAnnotationPresent(Foreign.class)) {

					Foreign annotation_foreign = field.getAnnotation(Foreign.class);

					DescribeForeign foreign = new DescribeForeign(annotation_foreign.ForeignClass(), annotation_foreign.ForeignAttributeName());
					added_datafield.setForeign(foreign);

				}

				fields.add(added_datafield);


			}

		}


		//		Récupération des fields privés via la méthode getPrivateFields

		//		Le checking est fait dans la fonction coherenceChecking

		try {

			System.out.println("Nb de private fields : " + private_fields.size());

			for(DescribeField field : private_fields) {
				DataField added_datafield = new DataField(field.getType(), field.getNameInDb(),field.getField_type(),  field.getClassField_name(), mon_entite, table_name);

				field.getConstraints().forEach(constraint -> added_datafield.getConstraints().add(constraint));

				added_datafield.setForeign(field.getForeign());

				fields.add(added_datafield);

			}

		} catch (IllegalArgumentException |  SecurityException e) {
			// Cette exception est déja check par le coherence checking, elle ne peut pas arriver
			e.printStackTrace();
		}

		return fields;

	}



	public static void coherenceChecking(Class<? extends Entity> mon_entite, List<DescribeField> private_fields) throws DaoObjectNotValidException {

		if(!mon_entite.isAnnotationPresent(Table.class)) {
			throw new DaoObjectNotValidException("This object is not declared has a database table");
		}		


		try {
			if(mon_entite.getMethod("getPrivateFields") == null 
					|| mon_entite.getMethod("getPrivateFields").getReturnType() != List.class
					|| !Modifier.isStatic(mon_entite.getMethod("getPrivateFields").getModifiers())) {	
				throw new DaoObjectNotValidException("You shoud have a method public static List<DescribeField> getPrivateFields() in your class " + mon_entite);
			}

		} catch (NoSuchMethodException | SecurityException  e1) {
			e1.printStackTrace();
			//			throw new DaoObjectNotValidException("You shoud have a method public static List<DescribeField> getPrivateFields in your class");
		}


		//		Check que pour chaque field déclaré, il soit publique, ou ai un getter/setter.
		try {
			
			for(DescribeField column : private_fields){
				try {
					mon_entite.getMethod(StringConversions.toLowerCamelCase("get_"+column.getClassField_name()));
					mon_entite.getMethod(StringConversions.toLowerCamelCase("set_"+column.getClassField_name()), column.getField_type());

				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();

					throw new DaoObjectNotValidException("The field " + column.getClassField_name() + " is not public and doesnt have getter and setter \n"
							+ "put it public or create getter in the form of getMyField");
				}
			}	
		} catch (IllegalArgumentException | SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static void createTable(Class<? extends Entity> mon_entite, AbstractSQLFormatter formatter, List<DescribeField> private_fields) throws DaoObjectNotValidException {

		coherenceChecking(mon_entite, private_fields);

		String table_name = mon_entite.getAnnotation(Table.class).name();

		List<DataField> fields =  ORM.getFields(mon_entite, private_fields);

		formatter.createTable(table_name, fields);

	}

	public static void dropTable(Class<? extends Entity> mon_entite, AbstractSQLFormatter formatter) throws DaoObjectNotValidException {

		if(!mon_entite.isAnnotationPresent(Table.class)) {
			throw new DaoObjectNotValidException("This object is not declared has a database table");
		}		
		
		formatter.dropTable(mon_entite.getAnnotation(Table.class).name());

	}

}
