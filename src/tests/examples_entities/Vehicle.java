package tests.examples_entities;

import java.util.ArrayList;
import java.util.List;

import orm.DataTypes;
import orm.DescribeField;
import orm.Entity;
import orm.annotations.Column;
import orm.annotations.Id;
import orm.annotations.Table;

@Table(name="Vehicles")
public class Vehicle extends Entity {
	
	@Id
	@Column(datatype = DataTypes.ID, name = "id")
	public long id;
	
	@Column(datatype = DataTypes.STRING, name = "name")
	public String name;
	
	public Vehicle(){}
	
	public static List<DescribeField> getPrivateFields(){
		return new ArrayList<DescribeField>();
	}
}
