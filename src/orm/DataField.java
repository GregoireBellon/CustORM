package orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import orm.annotations.Column;
import orm.annotations.Table;

public class DataField {
	private DataTypes type; 
	private String name_in_db;
	private String class_field_name;
	private Class<? extends Entity> entity_class;
	private Class<?> class_field_type;
	private String table_name;
	
	private Object value;
	
	private List<Constraint> constraints;
	
	private DescribeForeign foreign;
	

	public DataField(DataTypes type, String name_in_db, Class<?> class_field_type ,String class_field_name,  Class<? extends Entity> entity_class, String table_name) {
		this.type = type;
		this.name_in_db = name_in_db;
		this.class_field_name = class_field_name;
		this.entity_class = entity_class;
		this.table_name = table_name;
		this.value = null;
		this.class_field_type = class_field_type;
		
		this.constraints = new ArrayList<Constraint>();
		
	}
	
//	Datafield n'est créé que par l'ORM, qui check en amont le fait que la classe soit bien une entité
//	Les classes utilisant le constructeur par recopie de DataField sont les SQLSerializer, qui recopie des DataFields créés par l'ORM
//	Bref, ce @SuppressWarnings est safe
	
	@SuppressWarnings("unchecked")
	public DataField(Field class_field) {
		
		this.type = class_field.getAnnotation(Column.class).datatype();
		this.name_in_db = class_field.getAnnotation(Column.class).name();
		this.class_field_name = class_field.getName();
		this.entity_class = (Class<? extends Entity>) class_field.getDeclaringClass();
		this.table_name = class_field.getDeclaringClass().getAnnotation(Table.class).name();
		this.value = null;
		
		this.class_field_type = class_field.getType();
		
		this.constraints = new ArrayList<Constraint>();
		
	}
	
	public DataField(DataField obj) {
		
		this(obj.getType(), obj.getName_in_db(), obj.class_field_type, obj.getClass_field_name(), obj.getEntity_class(), obj.getTableName());

	}

	public DataTypes getType() {
		return type;
	}

	public String getName_in_db() {
		return name_in_db;
	}
	
	public String getTableName() {
		return this.table_name;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return this.value;
		
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	public String getClass_field_name() {
		return class_field_name;
	}

	public Class<? extends Entity> getEntity_class() {
		return entity_class;
	}
	
	public void setForeign(DescribeForeign foreign) {
		this.foreign = foreign;
		this.constraints.add(Constraint.FOREIGN);
	}
	
	public DescribeForeign getForeign() {
		return foreign;
	}

	public Class<?> getClass_field_type() {
		return class_field_type;
	}
	
}
