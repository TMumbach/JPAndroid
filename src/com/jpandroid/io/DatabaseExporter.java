package com.jpandroid.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.os.Environment;
import android.util.Log;

public class DatabaseExporter
{

	private static final String TAG = "D4F.DatabaseExporter";

	public static boolean export(String currentDBPath, String backupDBPath, String backupName)
	{

		if (!DatabaseExporter.createDir(backupDBPath))
		{
			Log.i(TAG, "The directory cannot be created: " + backupDBPath);
			return false;
		}

		try
		{
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite())
			{
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath + "/" + backupName);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();

				Log.i(TAG, "Export complete");
			}
		} catch (Exception e)
		{
			Log.e(TAG, e.getMessage());

			return false;
		}

		return true;
	}

	private static boolean createDir(String backupDBPath)
	{
		File direct = new File(backupDBPath);

		if (!direct.exists())
		{
			if (direct.mkdirs())
			{
				return true;
			} else
			{
				return false;
			}
		} else
		{
			return true;
		}
	}
}
