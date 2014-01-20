package com.jpandroid.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jpandroid.annotations.Column;
import com.jpandroid.annotations.FieldsToLoad;
import com.jpandroid.annotations.ForeignKey;
import com.jpandroid.annotations.NamedQuery;
import com.jpandroid.annotations.Primarykey;
import com.jpandroid.annotations.Queries;
import com.jpandroid.annotations.Table;
import com.jpandroid.annotations.Transient;
import com.jpandroid.criteria.Query;
import com.jpandroid.entity.Entity;
import com.jpandroid.log.Logger;
import com.jpandroid.types.ColumnType;
import com.jpandroid.types.DateType;
import com.jpandroid.types.GenerationType;
import com.jpandroid.types.LoadType;


public class ORMUtils
{
	@SuppressWarnings("all")
	public static ContentValues extractData(Entity entity)
	{
		ContentValues values = new ContentValues();

		Class<?> clazz = entity.getClass().asSubclass(entity.getClass());
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields)
		{
			Object value = null;
			Primarykey primarykey = field.getAnnotation(Primarykey.class);
			field.setAccessible(true);
			try
			{
				if (primarykey != null)
				{
					if (primarykey.strategy() == GenerationType.NONE && field.get(entity) == null)
					{
						Logger.showError("Primary key must contain value");
						return null;
					}
				}
				String fieldName = getColumnName(field);

				value = field.get(entity);

				if (value != null)
				{
					if (value instanceof Entity)
					{
						getForeignKeyValue(values, field, (Entity) value);
					}

					else
					{
						columnType(values, value, field, fieldName);
					}
				}
			} 
			catch (Exception e)
			{
				Logger.showError("extractData: " + e.getMessage());
			}
		}
		return values;
	}

	@SuppressWarnings("all")
	public static void getForeignKeyValue(ContentValues values, Field field, Entity entity)
	{
		ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
		String fieldName = foreignKey.name();
		String fieldFK = foreignKey.referencedColumnName();
		Field[] fields = entity.getClass().getDeclaredFields();

		for (Field f : fields)
		{
			String name = getColumnName(f);

			if (name.equalsIgnoreCase(fieldFK))
			{
				try
				{
					f.setAccessible(true);
					Object value = f.get(entity);
					columnType(values, value, f, fieldName);
				} 
				catch (Exception e)
				{
					Logger.showError("getForeignKeyValue" + e.getMessage());
				}
				return;
			}
		}
	}

	@SuppressWarnings("all")
	public static <T extends Entity> T extractDataFromCursorSingle(Cursor cursor, Class<T> entity, SQLiteDatabase mDatabase, String[] fieldsToLoad) throws Exception
	{
		Entity result = entity.newInstance();

		if (cursor.moveToFirst())
		{
			loadModel(entity, cursor, result, mDatabase, fieldsToLoad);
		} else
		{
			result = null;
		}

		return (T) result;
	}
	
	@SuppressWarnings("all")
	public static <T extends Entity> T extractDataFromCursorSingleLastInserted(Cursor cursor, Class<T> entity, SQLiteDatabase mDatabase, String[] fieldsToLoad) throws Exception
	{
		Entity result = entity.newInstance();
		
		if (cursor.moveToLast())
		{
			loadModel(entity, cursor, result, mDatabase, fieldsToLoad);
		} else
		{
			result = null;
		}
		
		return (T) result;
	}

	@SuppressWarnings("all")
	public static <T extends Entity> List<T> extractDataFromCursorList(Cursor cursor, Class<T> entity, SQLiteDatabase mDatabase, String[] fieldsToLoad) throws Exception
	{
		List<T> entities = new ArrayList<T>(0);

		if (cursor.moveToFirst())
		{
			do
			{
				Entity result = entity.newInstance();
				loadModel(entity, cursor, result, mDatabase, fieldsToLoad);
				entities.add((T) result);
			} while (cursor.moveToNext());
		}

		return entities;
	}

	@SuppressWarnings("all")
	private static void loadModel(Class<?> object, Cursor cursor, Entity model, SQLiteDatabase mDatabase, String[] fieldsToLoad)
	{
		Field[] fields = object.getDeclaredFields();

		Column column = null;
		ForeignKey foreignKey = null;
		
		List<Field> oneToManyFields = new ArrayList<Field>(0);

		for (Field field : fields)
		{
			column = field.getAnnotation(Column.class);

			String fieldName = column == null ? field.getName() : column.name();

			if (column == null)
			{
				foreignKey = field.getAnnotation(ForeignKey.class);

				if (foreignKey != null)
				{
					fieldName = foreignKey.name();
				}
			}

			Class<?> fieldType = field.getType();
			
			String[] columnNames = cursor.getColumnNames();

			for (String string : columnNames)
			{
				if (string.equalsIgnoreCase(fieldName))
				{
					fieldName = string;
				}
			}

			int columnIndex = cursor.getColumnIndex(fieldName);

			if (columnIndex < 0)
			{
				continue;
			}

			field.setAccessible(true);
			try
			{
				if ((fieldType.equals(Boolean.class)) || (fieldType.equals(Boolean.TYPE)))
				{
					field.set(model, Boolean.valueOf(cursor.getInt(columnIndex) != 0));
				}
				else if (fieldType.equals(Character.class))
				{
					if (cursor.getString(columnIndex) != null)
					{
						field.set(model, Character.valueOf(cursor.getString(columnIndex).charAt(0)));
					}
				}
				else if (fieldType.equals(Date.class))
				{
					if (cursor.getString(columnIndex) != null)
					{	
						if (column != null)
						{
							setDateTime(column, field, model, cursor, columnIndex);
						}
					}
				}
				else if ((fieldType.equals(Double.class)) || (fieldType.equals(Double.TYPE)))
				{
					field.set(model, Double.valueOf(cursor.getDouble(columnIndex)));
				}
				else if ((fieldType.equals(Float.class)) || (fieldType.equals(Float.TYPE)))
				{
					field.set(model, Float.valueOf(cursor.getFloat(columnIndex)));
				}
				else if ((fieldType.equals(Integer.class)) || (fieldType.equals(Integer.TYPE)))
				{
					field.set(model, Integer.valueOf(cursor.getInt(columnIndex)));
				}
				else if ((fieldType.equals(Long.class)) || (fieldType.equals(Long.TYPE)))
				{
					field.set(model, Long.valueOf(cursor.getLong(columnIndex)));
				}
				else if (fieldType.equals(String.class))
				{
					field.set(model, cursor.getString(columnIndex));
				}
				else if (fieldType.equals(Byte.class) || (fieldType.equals(Byte.TYPE)))
				{
					field.set(model, cursor.getBlob(columnIndex));
				}
				else if (foreignKey != null)
				{
					loadSubEntity(cursor, foreignKey, fieldType, columnIndex, model, field, mDatabase);
				}
			} 
			catch (Exception e)
			{
				Logger.showError("loadModel" + e.toString());
			}
		}
	}
	
	@SuppressWarnings("all")
	private static void setDateTime(Column column, Field field, Entity model, Cursor cursor, Integer columnIndex) throws Exception
	{
		if (column.dateType() == DateType.DATE)
		{
			field.set(model, new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(columnIndex)));
		} 
		else if (column.dateType() == DateType.DATE_TIME)
		{
			field.set(model, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(columnIndex)));
		} 
		else if (column.dateType() == DateType.STRF_TIME)
		{
			field.set(model, new Date(Long.valueOf(cursor.getString(columnIndex))));
		}
	}

	@SuppressWarnings("all")
	private static <T extends Entity> T findSubClass(Class<T> entity, String foreignName, String id, SQLiteDatabase mDatabase, String[] fieldsToLoad) throws Exception
	{
		String[] fields = fieldsToLoad == null ? ORMUtils.getColumns(entity) : fieldsToLoad;
		Cursor cursor = mDatabase.query(true, ORMUtils.getTableName(entity), fields, foreignName + " = " + id, null, null, null, null, null);
		Entity result = null;

		result = extractDataFromCursorSingle(cursor, entity, mDatabase, fields);

		return (T) result;
	}

	@SuppressWarnings("all")
	private static void loadSubEntity(Cursor cursor, ForeignKey foreignKey, Class<?> fieldType, Integer columnIndex, Entity model, Field field, SQLiteDatabase mDatabase) throws Exception 
	{
		Entity subClass = null;

		String referencedColumnName = foreignKey.referencedColumnName();

		String foreignKeyColumnValue = cursor.getString(columnIndex);
		
		if(foreignKeyColumnValue == null)
		{
			field.set(model, subClass);
			return;
		}

		if (foreignKey.load() == LoadType.ALL)
		{
			subClass = (Entity) fieldType.newInstance();

			if (foreignKey.referencedColumnType() == ColumnType.TEXT)
			{
				foreignKeyColumnValue = "'" + foreignKeyColumnValue + "'";
			}
			field.set(model, findSubClass(subClass.getClass(), referencedColumnName, foreignKeyColumnValue, mDatabase, null));
		}
		else if (foreignKey.load() == LoadType.ONLY_ID)
		{
			if(foreignKey.referencedColumnType() == ColumnType.NUMBER)
			{
				Number entityId = cursor.getInt(columnIndex);
				
				if(entityId != null)
				{
					subClass = (Entity) fieldType.newInstance();
					Field fieldSubClass = fieldType.getDeclaredField(referencedColumnName);
					fieldSubClass.setAccessible(true);
					fieldSubClass.set(subClass, entityId);
				}
			}
			else
			{
				if(foreignKeyColumnValue != null)
				{
					subClass = (Entity) fieldType.newInstance();
					Field fieldSubClass = fieldType.getDeclaredField(referencedColumnName);
					fieldSubClass.setAccessible(true);
					fieldSubClass.set(subClass, foreignKeyColumnValue);
				}
			}
			field.set(model, subClass);
		} 
		else if (foreignKey.load() == LoadType.NONE)
		{
			FieldsToLoad ftl = field.getAnnotation(FieldsToLoad.class);

			if (ftl != null)
			{
				subClass = (Entity) fieldType.newInstance();

				if (foreignKey.referencedColumnType() == ColumnType.TEXT)
				{
					foreignKeyColumnValue = "'" + foreignKeyColumnValue + "'";
				}

				field.set(model, findSubClass(subClass.getClass(), referencedColumnName, foreignKeyColumnValue, mDatabase, ftl.fields()));
			} 
			else
			{
				field.set(model, null);
			}
		}
	}

	public static void columnType(ContentValues values, Object value, Field field, String fieldName)
	{
		if (value instanceof String)
		{
			values.put(fieldName, (String) value);
		} 
		else if (value instanceof Character)
		{
			values.put(fieldName, (String) value);
		} 
		else if (value instanceof Integer)
		{
			values.put(fieldName, (Integer) value);
		} 
		else if (value instanceof Boolean)
		{
			values.put(fieldName, (Boolean) value);
		} 
		else if (value instanceof Long)
		{
			values.put(fieldName, (Long) value);
		} 
		else if (value instanceof Float)
		{
			values.put(fieldName, (Float) value);
		} 
		else if (value instanceof Double)
		{
			values.put(fieldName, (Double) value);
		} 
		else if (value instanceof Byte)
		{
			values.put(fieldName, (Byte) value);
		} 
		else if (value instanceof Date)
		{
			Column column = field.getAnnotation(Column.class);
			
			if (column != null)
			{
				if (column.dateType() == DateType.DATE)
				{
					values.put(fieldName, new SimpleDateFormat("yyyy-MM-dd").format((Date) value));
				} 
				else if (column.dateType() == DateType.DATE_TIME)
				{
					values.put(fieldName, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) value));
				} 
				else if (column.dateType() == DateType.STRF_TIME)
				{
					values.put(fieldName, String.valueOf(((Date) value).getTime()));
				}
			}
		}
	}
	
	@SuppressWarnings("all")
	public static <T extends Entity> T loadSubEntities(Entity entity, SQLiteDatabase db)
	{
		Field[] declaredFields = entity.getClass().getDeclaredFields();
		
		db.beginTransaction();
		
		for (Field field : declaredFields)
		{
			ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);

			if (foreignKey != null)
			{
				try
				{
					Field subField = entity.getClass().getDeclaredField(field.getName());
					subField.setAccessible(true);
					Object value = subField.get(entity);
					
					if(value == null)
					{
						continue;
					}

					Field referencedColumnNameField = value.getClass().getDeclaredField(foreignKey.referencedColumnName());
					referencedColumnNameField.setAccessible(true);
					Object object = referencedColumnNameField.get(value);

					String[] columns = ORMUtils.getColumns(field.getType());

					if (foreignKey.referencedColumnType() == ColumnType.TEXT)
					{
						object = "'" + object + "'";
					}

					Cursor cursor = db.query(true, ORMUtils.getTableName(field.getType()), columns, foreignKey.referencedColumnName() + " = " + object, null, null, null, null, null);

					T result = ORMUtils.extractDataFromCursorSingle(cursor, (Class<T>) field.getType(), db, null);

					field.setAccessible(true);
					field.set(entity, result);
					
					cursor.close();
					db.yieldIfContendedSafely();
					
				}
				catch (Exception e)
				{
					Logger.showError("loadSubEntities " + e.getMessage());
				}
			}
		}
		
		db.setTransactionSuccessful();
		
		return (T) entity;
	}

	public static String getColumnName(Field field)
	{
		Column column = field.getAnnotation(Column.class);
		return column == null ? field.getName() : column.name().equals("") ? field.getName() : column.name();
	}

	public static String getTableName(Class<?> entity)
	{
		Table table = entity.getAnnotation(Table.class);
		return table.value();
	}
	
	public static String[] getColumns(Class<?> clazz)
	{
		Field[] fields = clazz.getDeclaredFields();

		List<String> columns = new ArrayList<String>(0);

		for (Field field : fields)
		{
			if (field.getAnnotation(Transient.class) != null)
			{
				continue;
			}
			
			field.getAnnotation(ForeignKey.class);
			ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
			Column column = field.getAnnotation(Column.class);
			String name = foreignKey == null ? column == null ? field.getName() : column.name() : foreignKey.name();

			if (!Modifier.isFinal(field.getModifiers()))
			{
				columns.add(name);
			}
		}
		return columns.toArray(new String[columns.size()]);
	}
	
	public static String replaceParameters(String query, Map<String, Object> parameters) 
	{
		if(parameters == null)
		{
			return null;
		}
		
		for (Object key : parameters.keySet()) 
		{
			query = query.replace(key.toString(), parameters.get(key).toString());
		}
		
		return query;
	}
	
	public static Query getQueryByNamedQuery(String name, Class<?> type,  Map<String, Object> parameters)
	{
		NamedQuery[] namedQueries = type.getAnnotation(Queries.class).value();
		
		for (NamedQuery namedQuery : namedQueries) 
		{
			if(namedQuery.name().equals(name)) 
			{
				Query query = new Query(type);
				
				query.setColumns(namedQuery.columns().length == 0 ? ORMUtils.getColumns(type) : namedQuery.columns());
				
				if(!namedQuery.selection().equals(""))
				{
					query.setSelection(ORMUtils.replaceParameters(namedQuery.selection(), parameters));
				}
				
				return query;
			}
		}
		
		return null;
	}
	
	public static Map<String, Object> getMap(Object...args)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		
		List<Object> valores = Arrays.asList(args);
		
		Iterator<Object> iterator = valores.iterator();
		
		while(iterator.hasNext()) {
			String key = iterator.next().toString();
			Object value = iterator.next();
			map.put(key, value);
		}
		
		return map;
	}
}