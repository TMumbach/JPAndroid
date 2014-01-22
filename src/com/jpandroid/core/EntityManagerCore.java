package com.jpandroid.core;

import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jpandroid.criteria.Query;
import com.jpandroid.database.DatabaseManager;
import com.jpandroid.entity.Entity;
import com.jpandroid.log.Logger;
import com.jpandroid.types.SQLiteTypes;

public class EntityManagerCore implements EntityManager
{
	private DatabaseManager mDatabaseManager;

	public static String mLock = "dblock";

	private EntityManagerCore(DatabaseManager databaseManager)
	{
		Logger.show("JPAndroid is Running...");

		this.mDatabaseManager = databaseManager;
	}
	
	public static EntityManagerCore getInstance(DatabaseManager databaseManager)
	{
		return new EntityManagerCore(databaseManager);
	}

	@Override
	@SuppressWarnings("all")
	public Entity insert(Entity entity)
	{
		if (entity.getId() != null)
		{
			return update(entity);
		}
		else
		{
			return persist(entity);
		}
	}

	@Override
	@SuppressWarnings("all")
	public void delete(Entity entity)
	{
		String where = SQLiteTypes.UPDATE_DELETE_ENTITY;
		String[] whereArgs = new String[] { String.valueOf(entity.getId()) };

		synchronized (mLock)
		{
			SQLiteDatabase db = this.mDatabaseManager.open();
			try
			{
				db.delete(ORMUtils.getTableName(entity.getClass()), where, whereArgs);
			}
			catch (Exception e)
			{
				Logger.showWarning(e.getMessage());
			}
			finally
			{
				this.mDatabaseManager.close();
			}
		}
	}

	@Override
	@SuppressWarnings("all")
	public void delete(Class<?> entity, Object id)
	{
		String where = SQLiteTypes.UPDATE_DELETE_ENTITY;
		String[] whereArgs = new String[] { String.valueOf(id) };

		synchronized (mLock)
		{
			SQLiteDatabase db = this.mDatabaseManager.open();
			try
			{
				db.delete(ORMUtils.getTableName(entity), where, whereArgs);
			}
			catch (Exception e)
			{
				Logger.showWarning(e.getMessage());
			}
			finally
			{
				this.mDatabaseManager.close();
			}
		}
	}

	@Override
	@SuppressWarnings("all")
	public void insert(List<Entity> entities)
	{
		synchronized (mLock)
		{
			SQLiteDatabase db = this.mDatabaseManager.open();
			try
			{
				db.beginTransaction();
				for (Entity entity : entities)
				{
					insert(entity);
					db.yieldIfContendedSafely();
				}
				db.setTransactionSuccessful();
			}
			catch (Exception e)
			{
				Logger.showWarning(e.getMessage());
			}
			finally
			{
				db.endTransaction();
				this.mDatabaseManager.close();
			}
		}
	}

	@Override
	@SuppressWarnings("all")
	public <T extends Entity> List<T> select(Class<T> type, Query query)
	{
		List<T> result = null;

		synchronized (mLock)
		{
			SQLiteDatabase db = this.mDatabaseManager.open();
			try
			{
				Cursor cursor = query.getCursor(db);
				result = ORMUtils.extractDataFromCursorList(cursor, type, db, null);
				cursor.close();
			}
			catch (Exception e)
			{
				Logger.showWarning(e.getMessage());
			}
			finally
			{
				db.close();
			}
		}
		return result;
	}
	
	@Override
	@SuppressWarnings("all")
	public <T extends Entity> List<T> select(Class<T> returnType, String selection)
	{
		return select(returnType, new Query(returnType, selection));
	}

	@Override
	@SuppressWarnings("all")
	public <T extends Entity> List<T> select(Class<T> returnType, String selection, String... columns)
	{
		return select(returnType, new Query(returnType, selection, columns));
	}

	@Override
	@SuppressWarnings("all")
	public <T extends Entity> T selectById(Class<T> entity, Object id)
	{
		String[] columns = ORMUtils.getColumns(entity);

		T result = null;

		synchronized (mLock)
		{
			SQLiteDatabase db = this.mDatabaseManager.open();
			try
			{
				db.beginTransaction();
				Cursor cursor = db.query(true, ORMUtils.getTableName(entity), columns, "id = " + id, null, null, null, null, null);
				result = ORMUtils.extractDataFromCursorSingle(cursor, entity, db, null);
				cursor.close();
			}
			catch (Exception e)
			{
				Logger.showWarning(e.getMessage());
			}
			finally
			{
				db.endTransaction();
				db.close();
			}
		}
		return result;
	}

	@Override
	@SuppressWarnings("all")
	public <T extends Entity> T selectSingle(Class<T> type, Query query)
	{
		List<T> select = select(type, query);
		return select != null ? (select.size() > 0 ? select.get(0) : null) : null;
	}
	
	@Override
	@SuppressWarnings("all")
	public <T extends Entity> T selectSingle(Class<T> returnType, String selection)
	{
		return selectSingle(returnType, new Query(returnType, selection));
	}

	@Override
	@SuppressWarnings("all")
	public <T extends Entity> T selectSingle(Class<T> returnType, String selection, String... columns)
	{
		return selectSingle(returnType, new Query(returnType, selection, columns));
	}
	
	@Override
	@SuppressWarnings("all")
	public <T extends Entity> T loadEntity(Class<T> returnType, Entity entity)
	{
		synchronized (mLock)
		{
			SQLiteDatabase db = this.mDatabaseManager.open();

			try
			{
				entity = ORMUtils.loadSubEntities(entity, db);
			}
			catch (Exception e)
			{
				Logger.showWarning(e.getMessage());
			}
			finally
			{
				db.endTransaction();
				db.close();
			}
		}

		return (T) entity;
	}

	@Override
	public <T> Integer count(Query query)
	{
		Integer queryNumEntries = 0;

		synchronized (mLock)
		{
			SQLiteDatabase db = this.mDatabaseManager.open();
			try
			{
				db.beginTransaction();
				Cursor cursor = query.getCursor(db);
				queryNumEntries = cursor.getCount();
			}
			catch (Exception e)
			{
				Logger.showWarning(e.getMessage());
			}
			finally
			{
				db.endTransaction();
				db.close();
			}
		}
		return queryNumEntries;
	}

	@Override
	@SuppressWarnings("all")
	public <T> T max(Class<T> type, Query query)
	{
		T maxValue = null;

		synchronized (mLock)
		{
			SQLiteDatabase db = this.mDatabaseManager.open();
			try
			{
				db.beginTransaction();
				String string = "case when max(" + query.getColumns()[0] + ") is null then 0 else max(" + query.getColumns()[0] + ") end as max";
				query.getColumns()[0] = string;
				Cursor cursor = query.getCursor(db);
				if (cursor.getCount() > 0)
				{
					cursor.moveToFirst();

					if ((type.equals(Integer.class)) || (type.equals(Integer.TYPE)))
					{
						Integer max = cursor.getInt(0);
						maxValue = (T) max;

					}
					if ((type.equals(Double.class)) || (type.equals(Double.TYPE)))
					{
						Double max = cursor.getDouble(0);
						maxValue = (T) max;
						
					}
					else if ((type.equals(Long.class)) || (type.equals(Long.TYPE)))
					{
						Long max = cursor.getLong(0);
						maxValue = (T) max;
					}

					cursor.close();
				}
				cursor.close();
			}
			catch (Exception e)
			{
				Logger.showWarning(e.getMessage());
			}
			finally
			{
				db.endTransaction();
				db.close();
			}
		}
		return maxValue;
	}

	@Override
	@SuppressWarnings("all")
	public <T> T sum(Class<T> type, Query query)
	{
		T maxValue = null;

		String[] columns = new String[] { "sum(" + query.getColumns()[0] + ") as sum" };
		
		query.setColumns(columns);
		
		synchronized (mLock)
		{
			SQLiteDatabase db = this.mDatabaseManager.open();
			try
			{
				db.beginTransaction();
				Cursor cursor = query.getCursor(db);

				if (cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					if ((type.equals(Integer.class)) || (type.equals(Integer.TYPE)))
					{
						Integer max = cursor.getInt(0);
						maxValue = (T) max;

					}
					if ((type.equals(Double.class)) || (type.equals(Double.TYPE)))
					{
						Double max = cursor.getDouble(0);
						maxValue = (T) max;
						
					}
					else if ((type.equals(Float.class)) || (type.equals(Float.TYPE)))
					{
						Float max = cursor.getFloat(0);
						maxValue = (T) max;
					}
					cursor.close();
				}
				cursor.close();
			}
			catch (Exception e)
			{
				Logger.showWarning(e.getMessage());
			}
			finally
			{
				db.endTransaction();
				db.close();
			}
		}
		return maxValue;
	}

	@SuppressWarnings("all")
	public Entity update(Entity entity)
	{
		Class<?> asSubclass = entity.getClass().asSubclass(entity.getClass());
		String tableName = ORMUtils.getTableName(asSubclass);

		synchronized (mLock)
		{
			SQLiteDatabase db = this.mDatabaseManager.open();
			try
			{
				String id = entity.getId().toString();
				ContentValues values = ORMUtils.extractData(entity);
				db.update(tableName, values, SQLiteTypes.UPDATE_DELETE_ENTITY, new String[] { id });
				
				Cursor cursor = db.query(ORMUtils.getTableName(entity.getClass()), ORMUtils.getColumns(entity.getClass()), null, null, null, null, null);
				
				cursor.moveToLast();
				
				entity = (Entity) ORMUtils.extractDataFromCursorSingleLastInserted(cursor, entity.getClass(), db, null);
				
				cursor.close();
			}
			catch (Exception e)
			{
				Logger.showWarning(e.getMessage());
			}
			finally
			{
				this.mDatabaseManager.close();
			}
		}
		
		return entity;
	}

	@SuppressWarnings("all")
	private Entity persist(Entity entity)
	{
		Class<?> asSubclass = entity.getClass().asSubclass(entity.getClass());
		String tableName = ORMUtils.getTableName(asSubclass);
		
		synchronized (mLock)
		{
			SQLiteDatabase db = this.mDatabaseManager.open();
			try
			{
				ContentValues values = ORMUtils.extractData(entity);
				db.insert(tableName, null, values);
				
				Cursor cursor = db.query(ORMUtils.getTableName(entity.getClass()), ORMUtils.getColumns(entity.getClass()), null, null, null, null, null);
				
				cursor.moveToLast();
				
				entity = (Entity) ORMUtils.extractDataFromCursorSingleLastInserted(cursor, entity.getClass(), db, null);
				
				cursor.close();
				
			}
			catch (Exception e)
			{
				Logger.showWarning(e.getMessage());
			}
			finally
			{
				this.mDatabaseManager.close();
			}
		}
		return entity;
	}

	@Override
	@SuppressWarnings("all")
	public <T extends Entity> List<T> selectListByNamedQuery(Class<T> type, String name, Map<String, Object> parameters) 
	{
		Query query = ORMUtils.getQueryByNamedQuery(name, type, parameters);
		
		if(query == null)
		{
			return null;
		}
		
		return select(type, query);
	
	}

	@Override
	@SuppressWarnings("all")
	public <T extends Entity> T selectSingleByNamedQuery(Class<T> type, String name, Map<String, Object> parameters) 
	{
		Query query = ORMUtils.getQueryByNamedQuery(name, type, parameters);
		
		if(query == null)
		{
			return null;
		}
		
		return selectSingle(type, query);
	}
	
}
