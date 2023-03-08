package orm;

import java.util.ArrayList;
import java.util.List;

public class DescribeField {
	private String name_in_db;
	private DataTypes type;
	private List<Constraint> constraints;
	private DescribeForeign foreign;
	private String classField_name;
	private Class<?> field_type;
	
	public DescribeField(String name_in_db, Class<?> field_type, String class_field_name,  DataTypes type, List<Constraint> constraints, DescribeForeign foreign) {
		this.name_in_db = name_in_db;
		this.type = type;
		this.constraints = constraints;
		this.foreign = foreign;
		this.classField_name = class_field_name;
		this.field_type = field_type;
	}
	
	public DescribeField(String name_in_db,  Class<?> field_type, String class_field_name, DataTypes type) {
		
		this(name_in_db, field_type, class_field_name, type, new ArrayList<Constraint>(), null);
		
	}
	
	
	
	public String getNameInDb() {
		return name_in_db;
	}
	
	public DataTypes getType() {
		return this.type;
	}
	
	public List<Constraint> getConstraints() {
		return constraints;
	}
	
	public String getClassField_name() {
		return classField_name;
	}
	
	public DescribeForeign getForeign() {
		return foreign;
	}

	public Class<?> getField_type() {
		return field_type;
	}
	
}
