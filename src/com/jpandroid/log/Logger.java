package com.jpandroid.log;

import com.jpandroid.database.DatabaseManager;

import android.util.Log;

public class Logger
{
	public static void show(Object value)
	{
		if(DatabaseManager.showLog)
			Log.i("D4F", value.toString());
	}
	
	public static void showError(Object value)
	{
		if(DatabaseManager.showLog)
			Log.e("D4F", value.toString());
	}
	
	public static void showWarning(Object value)
	{
		if(DatabaseManager.showLog)
			Log.w("D4F", value.toString());
	}
}
