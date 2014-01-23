package com.jpandroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.jpandroid.log.Logger;

/**
 * @author Tiago
 */
public class DatabaseManager
{
	private DatabaseHelper mDatabaseHelper;

	private SQLiteDatabase mDataBase;
	
	public static boolean showLog = false;

	/**
	 * Don't forget Manifest.xml: 
	 * 
	 * <code>  
	 * 	<ul>
	 * 		<li>android:name="db_version" android:value="1"</li>
     * 		<li>android:name="db_name" android:value="your_database.db"</li>
     * 	</ul>
	 * </code>
	 * 
	 * @param context your application context
	 * @param array with all entities
	 */
	public DatabaseManager(Context context, Class<?>...mappedEntities)
	{
		mDatabaseHelper = new DatabaseHelper(context, mappedEntities);
	}
	
	/**
	 * Auto scan tables or data base will be created in SDcard (Don't forget that you need to update the permissions for your Manifest file)
	 * </br>
	 * </br>
	 * Don't forget Manifest.xml: 
	 * <code>  
	 * 	<ul>
	 * 		<li>android:name="db_version" android:value="1"</li>
     * 		<li>android:name="db_name" android:value="your_database.db"</li>
     * 		<li>android:name="db_path" android:value="/MyApp/"</li> 
     * 	</ul>
	 * </code>
	 * 
	 * @param context
	 * @param dataBasePath
	 * @param autoScanTables 
	 */
	public DatabaseManager(Context context, boolean autoScanTables)
	{
		if(autoScanTables) 
		{
			mDatabaseHelper = new DatabaseHelper(context, autoScanTables);
		}
		else
		{
			mDatabaseHelper = new DatabaseHelper(context);
		}
	}

	public SQLiteDatabase getDatabase()
	{
		return mDataBase;
	}

	public SQLiteDatabase open()
	{
		return mDataBase = mDatabaseHelper.getWritableDatabase();
	}

	public void close()
	{
		if (mDataBase != null)
		{
			mDataBase.close();
			mDataBase = null;
		}
	}

	public void closeDatabaseHelper()
	{
		Logger.show("closing DatabaseHelper..");

		if (mDatabaseHelper != null)
		{
			mDatabaseHelper.close();
			mDatabaseHelper = null;
		}
	}
	
}
