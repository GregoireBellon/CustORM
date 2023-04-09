package orm.SQLFormatters;

import java.math.BigInteger;
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
import orm.annotations.Table;
import orm.exceptions.NoResult;
import orm.selection.Comparator;
import orm.selection.Selector;

public class MySQLFormatter extends AbstractSQLFormatter {


	public MySQLFormatter(String db_url) throws SQLException  {
		super(db_url);

	}

	@Override
	public BigInteger insert(String table_name, List<DataField> fields) {

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

		BigInteger ret = BigInteger.valueOf(0);

		Connection con = null;
		PreparedStatement preparedStmt = null;


		try {

			con = this.getRawSQLConnection();
			preparedStmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

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

			ret = BigInteger.valueOf(result.getLong(1));

			preparedStmt.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}finally {

			try {
				if(con!=null)
					con.close();

				if(preparedStmt!=null)
					preparedStmt.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}



		return ret;
	}

	@Override
	public void selectOne(String table_name, List<DataField> fields, List<List<Selector>> selectors) throws NoResult  {

		String query = "SELECT * FROM " + table_name + " ";

		query += whereQuery(selectors);

		query +=   "LIMIT 1 ;";

		System.out.println("Final query : " + query);

		Connection con = null;
		PreparedStatement st = null;		

		try {

			con = this.getRawSQLConnection();
			st = con.prepareStatement(query);

			ResultSet rs = st.executeQuery();

			//			Vérifie que le ResultSet n'est pas empty (ce nom de fonction n'a aucun sens)
			if(!rs.isBeforeFirst()) {
				throw new NoResult();
			}

			rs.next();

			for(DataField field : fields) {
				field.setValue(rs.getObject(field.getName_in_db()));		
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if(con!=null)
					con.close();

				if(st!=null)
					st.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}


	@Override
	public List<List<DataField>> selectMultiple(String table_name, List<DataField> fields, List<List<Selector>> selectors,
			int limit) throws NoResult {

		String query = "SELECT * FROM " + table_name + " ";
		query += whereQuery(selectors);
		query +=  (limit == -1)? ";" : "LIMIT " + limit + ";";

		Connection con = null;
		PreparedStatement st = null;
		List<List<DataField>> ret = new ArrayList<List<DataField>>();


		try {

			con = this.getRawSQLConnection();
			st = con.prepareStatement(query);

			ResultSet rs = st.executeQuery();

			if(!rs.isBeforeFirst()) {
				throw new NoResult();
			}

			while(rs.next()) {

				List<DataField> line = new ArrayList<DataField>();



				for(DataField field : fields) {

					DataField created_field = new DataField(field);

					created_field.setValue(rs.getObject(field.getName_in_db()));		

					line.add(created_field);
				}

				ret.add(line);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {

			try {

				if(con!=null)
					con.close();

				if(st!=null)
					st.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;

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

		Connection con = null;
		PreparedStatement preparedStmt = null;


		try {

			con = this.getRawSQLConnection();
			preparedStmt = con.prepareStatement(query);

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
		}finally {

			try {

				if(con!=null)
					con.close();

				if(preparedStmt!=null)
					preparedStmt.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}


	}

	
	@Override
	public void delete(String table_name, List<List<Selector>> selectors) {
		
		String query_content = "DELETE FROM " + table_name + " ";
		
		query_content += whereQuery(selectors);
		
		Connection con = null;
		PreparedStatement preparedStmt = null;

		try {
			
			con = this.getRawSQLConnection();
			preparedStmt = con.prepareStatement(query_content);

			preparedStmt.execute();
			preparedStmt.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}finally {

			try {

				if(con!=null)
					con.close();

				if(preparedStmt!=null)
					preparedStmt.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
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

		
		List<DataField> unique_fields = fields.stream().filter(f -> f.getConstraints().contains(Constraint.UNIQUE)).toList();
		
		for(DataField field : unique_fields) {
			query_content +=", ";
			query_content += "UNIQUE ("+field.getName_in_db() + ")";
		}
		
		List<DataField> foreign_keys = fields.stream().filter(f -> f.getForeign()!=null).toList();

		
		for(int i = 0; i < foreign_keys.size(); i++) {
			DataField field = foreign_keys.get(i);

			try {
				query_content += ", ";

				query_content += " FOREIGN KEY ("+field.getName_in_db() + ") REFERENCES " + field.getForeign().getForeign_class().getAnnotation(Table.class).name() + "(" +  field.getForeign().getForeign_attribute_name() + ")" ;

			} catch (SecurityException e) {
				System.err.println("The field " + field + " is badly associated with it's foreign key");
			}
		}



		query_content += ");";

		System.out.println("Generated query : " + query_content);

		Connection con = null;
		PreparedStatement s = null;

		int ret = 0;

		try {
			con = this.getRawSQLConnection();
			s =  con.prepareStatement(query_content);

			ret =  s.executeUpdate();
			s.close();


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {

			try {

				if(con!=null)
					con.close();

				if(s!=null)
					s.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}

		return ret;

	}

	@Override
	public int dropTable(String table_name) {

		Connection con = null;
		PreparedStatement s = null;

		try {

			con = this.getRawSQLConnection();

			s =  con.prepareStatement("DROP TABLE IF EXISTS " + table_name  +" ;");
			//			s.setString(1, table_name);

			System.out.println("Request : "  + s.toString());


			int ret = s.executeUpdate();
			s.close();
			return ret;

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {

			try {

				if(con!=null)
					con.close();

				if(s!=null)
					s.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	public long count(String table_name, List<List<Selector>> selectors) {
	
		String query = "SELECT COUNT(*) FROM " + table_name + " " + whereQuery(selectors) + ";";
		
		long ret = 0;
		
		Connection con = null;
		PreparedStatement s = null;		
		
		try {
			con = this.getRawSQLConnection();
			s =  con.prepareStatement(query);

			ResultSet result =  s.executeQuery();
			
			result.next();
			
			ret = result.getLong(1);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {

			try {

				if(con!=null)
					con.close();

				if(s!=null)
					s.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}

		return ret;
	}
	
	@Override
	public Connection getRawSQLConnection() {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(this.db_url);


		} catch (ClassNotFoundException | SQLException e) {

			System.err.println("The mysql driver was not found, or the connexion didn't succeed");
			e.printStackTrace();

			return null;

		}

	}

	public static String whereQuery(List<List<Selector>> selectors) {

		String clause = "";

		if(selectors != null && !selectors.isEmpty()) {

			clause += "WHERE ";

			for(Iterator<List<Selector>> it_list = selectors.iterator(); it_list.hasNext();) {
				List<Selector> li = it_list.next();

				for(Iterator<Selector> it = li.iterator(); it.hasNext();) {

					Selector sel = it.next();

					clause += sel.getFieldInDb() + " " + MySQLFormatter.comparatorConversion(sel.getComparator()) + " '" + sel.getComparated()+ "' ";

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
		case SHA256 : return "VARCHAR(65) NOT NULL";
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
