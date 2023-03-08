/**
 * 
 */
/**
 * @author etud
 *
 */
module custOrm {
	requires transitive java.sql;
	requires junit;
	requires org.junit.jupiter.api;
	exports orm;
	exports orm.annotations;
	exports orm.exceptions;
	exports orm.selection;
	exports orm.SQLFormatters;
	exports orm.utils;
}