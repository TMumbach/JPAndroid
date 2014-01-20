package com.jpandroid.database;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.jpandroid.annotations.Column;
import com.jpandroid.annotations.ForeignKey;
import com.jpandroid.annotations.Transient;
import com.jpandroid.annotations.Primarykey;
import com.jpandroid.annotations.Table;
import com.jpandroid.core.ORMUtils;
import com.jpandroid.entity.DomainEntity;
import com.jpandroid.entity.Entity;
import com.jpandroid.log.Logger;
import com.jpandroid.types.GenerationType;
import com.jpandroid.types.SQLiteTypes;

import dalvik.system.DexFile;

/**
 * @author Tiago
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
	private static final String TAG = "D4F.DatabaseHelper";

	private static final String db_name = "db_name";
	private static final String db_path = "db_path";
	private static final String db_version = "db_version";

	private Class<?>[] mEntities = null;

	public DatabaseHelper(Context context, Class<?>[] entities)
	{
		super(context, getDBName(context), null, getDBVersion(context));
		mEntities = entities;
	}
	
	public DatabaseHelper(Context context, boolean autoScan)
	{
		super(context, getDBName(context), null, getDBVersion(context));
		
		mEntities = scanTables(context);
	}

	public DatabaseHelper(Context context, String dataBasePath)
	{
		super(context, dataBasePath, null, getDBVersion(context));
	}
	
	public DatabaseHelper(Context context)
	{
		super(context, Environment.getExternalStorageDirectory().getPath() + getDBPath(context) + "/" + getDBName(context), null, getDBVersion(context));
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Logger.show("onCreate(" + db.getPath() + ")");

		createDataBase(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		if (mEntities == null)
		{
			return;
		}
		
		Logger.showWarning("dropping all tables from database: " + db.getPath());

		Class<?>[] entities = mEntities;

		for (Class<?> clazz : entities)
		{
			String tableName = ORMUtils.getTableName(clazz);
			if (tableName != null)
			{
				db.execSQL(SQLiteTypes.DROP_TABLE_IF_EXISTS + tableName);
			}
		}

		onCreate(db);
	}

	public void createDataBase(SQLiteDatabase db)
	{
		if (mEntities == null)
		{
			return;
		}
		
		Logger.show("creating all tables from database: " + db.getPath());

		Class<?>[] entities = mEntities;

		StringBuffer script = new StringBuffer("");

		for (Class<?> clazz : entities)
		{
			Table table = clazz.getAnnotation(Table.class);
			if (table == null)
			{
				continue;
			}
			script.append(SQLiteTypes.CREATE_TABLE + table.value());

			Field[] fields = clazz.getDeclaredFields();

			script.append("( \n");

			for (Field field : fields)
			{
				Primarykey primarykey = field.getAnnotation(Primarykey.class);
				if (primarykey != null)
				{
					String pkName = primarykey.name().equals("") ? field.getName() : primarykey.name();
					script.append(pkName + columnType(field.getType()) + SQLiteTypes.PRIMARY_KEY + getStrategy(primarykey.strategy()));
					break;
				}
			}

			for (Field field : fields)
			{
				if (field.getAnnotation(Transient.class) != null)
				{
					continue;
				}

				ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
				if (foreignKey != null)
				{
					script.append(",");
					script.append(foreignKey.name() + columnType(field.getType()));
				} else
				{
					if (Modifier.isFinal(field.getModifiers()))
					{
						continue;
					}
					if (field.getAnnotation(Primarykey.class) == null)
					{
						script.append(", ");
						Column column = field.getAnnotation(Column.class);
						String length = column == null ? "" : "(" + column.length() + ")";
						script.append("\n\t");
						script.append(ORMUtils.getColumnName(field) + columnType(field.getType()) + length);
					}
				}
			}

			for (Field field : fields)
			{
				if (field.getAnnotation(Transient.class) != null)
				{
					continue;
				}
				ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
				if (foreignKey != null)
				{
					script.append(", ");
					script.append("\n" + SQLiteTypes.FOREIGN_KEY + "(" + foreignKey.name() + ")" + SQLiteTypes.REFERENCES
							+ field.getType().getAnnotation(Table.class).value() + "(" + foreignKey.referencedColumnName() + ")");
				}
			}

			script.append(") \n");
			db.execSQL(script.toString());
			script = new StringBuffer("");
			
			Logger.show("Created table: " + table.value());
		}
		
		Logger.show("All tables has been created!");
	}

	private String columnType(Class<?> clazz)
	{

		if (clazz.equals(Integer.class) || clazz.equals(Long.class))
		{
			return SQLiteTypes.TYPE_INTEGER;
		}

		if (clazz.equals(String.class) || clazz.equals(Character.class))
		{
			return SQLiteTypes.TYPE_TEXT;
		}

		if (clazz.equals(Double.class) || clazz.equals(Float.class))
		{
			return SQLiteTypes.TYPE_REAL;
		}

		if (clazz.equals(BigDecimal.class))
		{
			return SQLiteTypes.TYPE_NUMERIC;
		}

		if (clazz.equals(Date.class))
		{
			return SQLiteTypes.TYPE_TEXT;
		}

		if (clazz.equals(Entity.class))
		{
			return SQLiteTypes.TYPE_INTEGER;
		}

		if (clazz.equals(Boolean.class))
		{
			return SQLiteTypes.TYPE_INTEGER;
		}

		return SQLiteTypes.TYPE_INTEGER;
	}

	private String getStrategy(GenerationType strategy)
	{
		if (strategy == GenerationType.AUTO_INCREMENT)
			return SQLiteTypes.AUTOINCREMENT;
		return "";
	}

	private static String getDBName(Context context)
	{
		String dbName = getMetaData(context, db_name);
		if (dbName == null)
		{
			dbName = "Application.db";
		}
		return dbName;
	}
	
	private static String getDBPath(Context context)
	{
		String dbName = getMetaData(context, db_path);
		if (dbName == null)
		{
			return "/JPAndroid/jpandroid.db";
		}
		return dbName;
	}

	private static int getDBVersion(Context context)
	{
		int dbVersion = getMetaDataInt(context, db_version);
		if (dbVersion > 0)
		{
			return dbVersion;
		}
		return 1;
	}

	private static String getMetaData(Context context, String name)
	{
		PackageManager pm = context.getPackageManager();
		try
		{
			ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 128);
			return ai.metaData.getString(name);

		} catch (Exception e)
		{
			Log.w(TAG, "Couldn't find meta data string: " + name);
		}
		return null;
	}

	private static int getMetaDataInt(Context context, String name)
	{
		PackageManager pm = context.getPackageManager();
		try
		{
			ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 128);
			return ai.metaData.getInt(name);
		} catch (Exception e)
		{
			Log.w(TAG, "Couldn't find meta data string: " + name);
		}
		return 0;
	}
	
	public Class<?>[] scanTables(Context context)
	{
		List<Class<?>> clazzList = new ArrayList<Class<?>>(0);
		
		try 
		{
			DexFile df = new DexFile(context.getPackageCodePath());
			for (Enumeration<String> iter = df.entries(); iter.hasMoreElements();) 
			{
				String s = iter.nextElement();

				try 
				{
					Class<?> clazz = Class.forName(s);

					if (DomainEntity.class.isAssignableFrom(clazz) && !s.equals("com.jpandroid.entity.DomainEntity")) 
					{
						Logger.show("Class found: " + s);
						clazzList.add(clazz);
					}

				} 
				catch (ClassNotFoundException e) 
				{
					Logger.showError(e.getMessage());
				}

			}
		} 
		catch (IOException e) 
		{
			Logger.showError(e.getMessage());
		}
		
		return clazzList.toArray(new Class[clazzList.size()]);
	}

}
