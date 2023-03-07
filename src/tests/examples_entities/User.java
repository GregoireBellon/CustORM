package tests.examples_entities;

import java.math.BigInteger;

import orm.DataTypes;
import orm.Entity;
import orm.annotations.Column;
import orm.annotations.Foreign;
import orm.annotations.Id;
import orm.annotations.NotNull;
import orm.annotations.Table;

@Table(name = "Users")
public class User extends Entity {

	@Id
	@Column(datatype = DataTypes.ID, name = "id")
	public BigInteger id;
	
	@NotNull
	@Column(datatype = DataTypes.STRING, name = "name")
	public String name;
	
	@Foreign(ForeignClass = Vehicle.class, ForeignAttributeName = "id")
	@Column(datatype = DataTypes.FOREIGN, name = "drives")
	public Vehicle vehicle;
	
	public User(){}
	
}
