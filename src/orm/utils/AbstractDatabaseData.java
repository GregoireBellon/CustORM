package orm.utils;

import java.sql.SQLException;

import orm.Entity;
import orm.ORM;
import orm.SQLFormatters.AbstractSQLFormatter;
import orm.SQLFormatters.SQLFormatterFactory;
import orm.exceptions.DaoObjectNotValidException;

public abstract class AbstractDatabaseData {



	public void setupDB(Class<? extends Entity>[] classes, boolean rebuild) throws SQLException {

		AbstractSQLFormatter formatter = SQLFormatterFactory.get(this);

		try {

			for(int i = classes.length-1; i >= 0; i--){

				Class<? extends Entity> ma_classe = classes[i];

				try {
					if(rebuild)
						ORM.dropTable(ma_classe, formatter);

				} catch (DaoObjectNotValidException e) {

					System.err.println( ma_classe.getName() + " is not a valid DAO object. DB can't be initialized");
					e.printStackTrace();
					System.exit(1);

				}

			}

			if(rebuild) {
				for(Class<? extends Entity> classe : classes){

					ORM.createTable(classe, formatter);

				}


			} }catch (IllegalArgumentException e) {
				System.out.println("Erreur lors de l'instanciation de la création en BDD : ");
				e.printStackTrace();
			} catch (DaoObjectNotValidException e) {

				// CETTE ERREUR NE PEUT PAS ARRIVER

				e.printStackTrace();
			}	
		}

		public abstract String getDatabasrUrl();

	}
