package orm;

import java.lang.reflect.Field;

import orm.annotations.Column;
import orm.annotations.Table;

public class DataField {
	private DataTypes type; 
	private String name_in_db;
	private Field class_field; 
	private String table_name;
	
	private Object value;
	

	public DataField(DataTypes type, String name_in_db, Field class_field, String table_name) {
		this.type = type;
		this.name_in_db = name_in_db;
		this.class_field = class_field;
		this.table_name = table_name;
		this.value = null;
	}
	
	public DataField(Field class_field) {
		this.class_field = class_field;
		
		this.type = class_field.getAnnotation(Column.class).datatype();
		this.name_in_db = class_field.getAnnotation(Column.class).name();
		this.table_name = class_field.getDeclaringClass().getAnnotation(Table.class).name();
		this.value = null;
	}
	
	public DataField(DataField obj) {
		
		this(obj.getType(), obj.getName_in_db(), obj.getClass_field(), obj.getTableName());
		
	}

	public DataTypes getType() {
		return type;
	}

	public String getName_in_db() {
		return name_in_db;
	}

	public Field getClass_field() {
		return class_field;
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
	
}
