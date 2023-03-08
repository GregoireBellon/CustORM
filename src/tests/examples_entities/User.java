package tests.examples_entities;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import orm.Constraint;
import orm.DataTypes;
import orm.DescribeField;
import orm.Entity;
import orm.annotations.Column;
import orm.annotations.Foreign;
import orm.annotations.Id;
import orm.annotations.Table;

@Table(name = "Users")
public class User extends Entity {

	@Id
	@Column(datatype = DataTypes.ID, name = "id")
	public BigInteger id;
	
	private String name;
	
	@Foreign(ForeignClass = Vehicle.class, ForeignAttributeName = "id")
	@Column(datatype = DataTypes.FOREIGN_ID, name = "drives")
	public BigInteger vehicle_id;
	
	public User(){}
	
	public static List<DescribeField> getPrivateFields(){
		List<DescribeField> ret = new ArrayList<DescribeField>();
		
		DescribeField name_field = new DescribeField("name", String.class, "name", DataTypes.STRING);
		name_field.getConstraints().add(Constraint.NOT_NULL);
		ret.add(name_field);
		
		return ret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
