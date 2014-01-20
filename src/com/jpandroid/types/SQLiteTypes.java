package com.jpandroid.types;

public abstract class SQLiteTypes
{

	public static final String CREATE_TABLE = "CREATE TABLE ";
	public static final String PRIMARY_KEY = " PRIMARY KEY";
	public static final String AUTOINCREMENT = " AUTOINCREMENT";
	public static final String FOREIGN_KEY = "FOREIGN KEY";
	public static final String REFERENCES = " REFERENCES ";
	public static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS  ";

	public static final String UPDATE_DELETE_ENTITY = "id = ?";

	/**
	 * Equivalente: INT, INTEGER, TINYINT, SMALLINT, MEDIUMINT, BIGINT, UNSIGNED
	 * BIG INT, INT2, INT8
	 */
	public static final String TYPE_INTEGER = " INTEGER";

	/** Equivalente: NUMERIC, DECIMAL(10,5), BOOLEAN, DATE, DATETIME */
	public static final String TYPE_NUMERIC = " NUMERIC(10,5)";

	/**
	 * Equivalente: CHARACTER(20), VARCHAR(255), VARYING CHARACTER(255),
	 * NCHAR(55), NATIVE CHARACTER(70), NVARCHAR(100), TEXT, CLOB
	 */
	public static final String TYPE_TEXT = " TEXT";

	/** Equivalente: REAL, DOUBLE, DOUBLE PRECISION, FLOAT */
	public static final String TYPE_REAL = " REAL";
	
	public static final String ASC = " ASC";
	public static final String DESC = " DESC";

}
