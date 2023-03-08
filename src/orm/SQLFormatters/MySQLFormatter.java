package orm.SQLFormatters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import orm.Constraint;
import orm.DataField;
import orm.DataTypes;
import orm.annotations.Column;
import orm.annotations.Foreign;
import orm.annotations.NotNull;
import orm.annotations.Table;
import orm.selection.Comparator;
import orm.selection.Selector;

public class MySQLFormatter extends AbstractSQLFormatter {


	private Connection con;

	public MySQLFormatter(String db_url) throws SQLException  {
		super(db_url);

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.con = DriverManager.getConnection(this.db_url);


		} catch (ClassNotFoundException e) {

			System.err.println("The mysql driver was not found, panicking");
			e.printStackTrace();

			System.exit(1);
		}
	}

	@Override
	public long insert(String table_name, List<DataField> fields) {

		System.out.println("Inserting");

		String query = "INSERT INTO " + table_name + "(";

		for(Iterator<DataField> it = fields.iterator(); it.hasNext();) {

			DataField elem = it.next();
			query += elem.getName_in_db();

			if(it.hasNext()) {
				query += ", ";
			}

		}

		query += ") values(";

		for(int i = 0; i < fields.size() - 1 ; i++){
			query += "?, ";
		}

		query += "?)";

		System.out.println("Final query : " + query);

		try {

			PreparedStatement preparedStmt;
			preparedStmt = this.con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			for(int i = 0; i < fields.size(); i++) {
				try {

					System.out.println("Adding the field " + fields.get(i).getName_in_db() + " with the value " + fields.get(i).getValue());
					preparedStmt.setObject(i+1, fields.get(i).getValue());
				} catch (SQLException e) {
					System.err.println("ORM : The " + fields.get(i).getName_in_db() + " type probably does not match with what has been provided.");
					e.printStackTrace();
				}
			}

			preparedStmt.execute();

			ResultSet result = preparedStmt.getGeneratedKeys();

			result.next();

			long ret = result.getLong(1);

			preparedStmt.close();

			return ret;

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		return 0;
	}

	@Override
	public void selectOne(String table_name, List<DataField> fields, List<List<Selector>> selectors)  {

		String query = "SELECT * FROM " + table_name + " ";

		query += whereQuery(selectors);

		query +=   "LIMIT 1 ;";

		System.out.println("Final query : " + query);

		PreparedStatement st;
		try {
			st = this.con.prepareStatement(query);

			ResultSet rs = st.executeQuery();

			if(rs.next()) {


				for(DataField field : fields) {
					field.setValue(rs.getObject(field.getName_in_db()));		
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	@Override
	public List<List<DataField>> selectMultiple(String table_name, List<DataField> fields, List<List<Selector>> selectors,
			int limit) {

		String query = "SELECT * FROM " + table_name + " ";

		query += whereQuery(selectors);

		query +=  (limit == -1)? ";" : "LIMIT " + limit + ";";

		PreparedStatement st;
		try {
			st = this.con.prepareStatement(query);

			ResultSet rs = st.executeQuery();

			List<List<DataField>> ret = new ArrayList<List<DataField>>();

			while(rs.next()) {

				List<DataField> line = new ArrayList<DataField>();



				for(DataField field : fields) {

					DataField created_field = new DataField(field);

					created_field.setValue(rs.getObject(field.getName_in_db()));		

					line.add(created_field);
				}

				ret.add(line);

			}

			return ret;


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}



	@Override
	public void update(String table_name, List<DataField> fields, List<List<Selector>> selectors) {

		System.out.println("updating...");

		String query = "UPDATE  " + table_name + " SET ";

		for(Iterator<DataField> it = fields.iterator(); it.hasNext();) {

			DataField elem = it.next();
			query += elem.getName_in_db() + " = ? ";

			if(it.hasNext()) {
				query += ", ";
			}

		}

		query += MySQLFormatter.whereQuery(selectors);


		System.out.println("Final query : " + query);

		try {

			PreparedStatement preparedStmt = this.con.prepareStatement(query);
			
			for(int i = 0; i < fields.size(); i++) {
				try {
				
				System.out.println("Adding the field " + fields.get(i).getName_in_db() + " with the value " + fields.get(i).getValue());
				preparedStmt.setObject(i+1, fields.get(i).getValue());
				} catch (SQLException e) {
					System.err.println("ORM : The " + fields.get(i).getName_in_db() + " type probably does not match with what has been provided.");
					e.printStackTrace();
				}
		}
			
		preparedStmt.execute();
		preparedStmt.close();

	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
}

@Override
public int createTable(String table_name, List<DataField> fields) {

	String query_content = "CREATE TABLE " + table_name + " (";

	for(int i = 0; i < fields.size(); i++) {
		DataField field = fields.get(i);

		query_content += (i != 0 ? ", " : "");

		query_content += this.fieldCreateLine(field);
	}

	List<DataField> foreign_keys = fields.stream().filter(f -> f.getForeign()!=null).toList();

	for(int i = 0; i < foreign_keys.size(); i++) {
		DataField field = foreign_keys.get(i);

		try {
			query_content += ", ";

			java.lang.reflect.Field foreign_field;

			query_content += " FOREIGN KEY ("+field.getName_in_db() + ") REFERENCES " + field.getForeign().getForeign_class().getAnnotation(Table.class).name() + "(" +  field.getForeign().getForeign_attribute_name() + ")" ;

		} catch (SecurityException e) {
			System.err.println("The field " + field + " is badly associated with it's foreign key");
		}
	}



	query_content += ");";

	System.out.println("Generated query : " + query_content);

	try {

		PreparedStatement s =  this.con.prepareStatement(query_content);

		int ret =  s.executeUpdate();
		s.close();

		return ret;

	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	return 0;
}

@Override
public int dropTable(String table_name) {

	try {

		PreparedStatement s =  this.con.prepareStatement("DROP TABLE IF EXISTS " + table_name  +" ;");
		//			s.setString(1, table_name);

		System.out.println("Request : "  + s.toString());


		int ret = s.executeUpdate();
		s.close();
		return ret;

	} catch (SQLException e) {
		e.printStackTrace();
	}

	return 0;
}


private String fieldCreateLine(DataField field) {

	String line = field.getName_in_db() +  " " +  datatypeConversion(field.getType());

	if(field.getConstraints().contains(Constraint.NOT_NULL)) {
		line += " NOT NULL";
	}

	return line;
}


@Override
public Connection getRawSQLConnection() {

	return this.con;

}

public static String whereQuery(List<List<Selector>> selectors) {

	String clause = "";

	if(selectors != null && !selectors.isEmpty()) {

		clause += "WHERE ";

		for(Iterator<List<Selector>> it_list = selectors.iterator(); it_list.hasNext();) {
			List<Selector> li = it_list.next();

			for(Iterator<Selector> it = li.iterator(); it.hasNext();) {

				Selector sel = it.next();

				clause += sel.getA().getName_in_db() + " " + MySQLFormatter.comparatorConversion(sel.getComparator()) + " " + sel.getB()+ " ";

				if(it.hasNext()) {
					clause += "AND ";
				}
			}

			if(it_list.hasNext()) {
				clause += "OR " ;
			}

		}
	}

	return clause;

}


public static String datatypeConversion(DataTypes datatype) {
	switch(datatype) {
	case BIGINT : return "BIGINT";
	case DATE : return "DATE";
	case ID : return "SERIAL PRIMARY KEY";
	case INT : return "INT";
	case FOREIGN_ID : return "BIGINT UNSIGNED";
	case BOOLEAN : return "BOOL";
	case FLOAT : return "FLOAT";
	case STRING : return "VARCHAR(50)";
	case UUID : return "UUID";
	case RAW : return "BLOB";
	}
	return null;
}

public static String comparatorConversion(Comparator comparator) {
	switch(comparator) {
	case DIFFERENT : return "!=";
	case EQUALS : return "=";
	case GREATER : return ">";
	case GREATER_EQ : return ">=";
	case LOWER : return "<";
	case LOWER_EQ : return "<=";

	default : return "=";
	}
}

}
