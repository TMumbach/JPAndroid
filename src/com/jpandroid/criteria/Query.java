package com.jpandroid.criteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jpandroid.core.ORMUtils;

/**
 * @author Tiago
 */
public class Query
{
	@SuppressWarnings("all")
	private Class<?> entity;
	
	private boolean distinct = false;
	
	private String selection;
	private String[] columns;
	private String[] selectionArgs;
	private String groupBy;
	private String having;
	private String orderBy;
	private String limit;

	private Map<String, String> transientColumn;

	public Query(Class<?> entity)
	{
		this.entity = entity;
		transientColumn = new HashMap<String, String>(0);
	}

	public Query(Class<?> entity, String selection)
	{
		this(entity);
		this.selection = selection;
	}

	public Query(Class<?> entity, String selection, String... columns)
	{
		this(entity, selection);
		this.columns = columns;
	}

	public Query(Class<?> entity, boolean distinct, String selection, String... columns)
	{
		this(entity, selection, columns);
		this.distinct = distinct;
	}
	
	public Class<?> getEntity()
	{
		return entity;
	}

	public void setEntity(Class<?> entity)
	{
		this.entity = entity;
	}

	public boolean isDistinct()
	{
		return distinct;
	}

	public void setDistinct(boolean distinct)
	{
		this.distinct = distinct;
	}

	public String getSelection()
	{
		return selection;
	}

	public String[] getColumns()
	{
		return columns;
	}

	public void setColumns(String[] columns)
	{
		this.columns = columns;
	}

	public void setSelection(String selection)
	{
		this.selection = selection;
	}

	public String[] getSelectionArgs()
	{
		return selectionArgs;
	}

	public void setSelectionArgs(String[] selectionArgs)
	{
		this.selectionArgs = selectionArgs;
	}

	public String getGroupBy()
	{
		return groupBy;
	}

	public void setGroupBy(String groupBy)
	{
		this.groupBy = groupBy;
	}

	public String getHaving()
	{
		return having;
	}

	public void setHaving(String having)
	{
		this.having = having;
	}

	public String getOrderBy()
	{
		return orderBy;
	}

	public void setOrderBy(String orderBy)
	{
		this.orderBy = orderBy;
	}

	public String getLimit()
	{
		return limit;
	}

	public void setLimit(String limit)
	{
		this.limit = limit;
	}

	public Map<String, String> getTransientColumn()
	{
		return transientColumn;
	}

	/**
	 * If you have a transient column, you can use this.
	 * @param transientColumn
	 * @param selection
	 */
	public void putTransientColumn(String transientColumn, String selection)
	{
		this.transientColumn.put(transientColumn, selection);
	}

	public Cursor getCursor(SQLiteDatabase database)
	{
		String[] columns;

		if (transientColumn.size() > 0)
		{
			columns = getColumnTransient();
		} 
		else
		{
			columns = this.columns == null ? ORMUtils.getColumns(entity) : this.columns;
		}

		return database.query(distinct, ORMUtils.getTableName(entity), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}

	private String[] getColumnTransient()
	{
		List<String> columnList = new ArrayList<String>(Arrays.asList(this.columns == null ? ORMUtils.getColumns(entity) : this.columns));
		
		for (Map.Entry<String, String> entry : transientColumn.entrySet())
		{
			columnList.add("(" + entry.getValue() + ")" + " as " + entry.getKey());
		}
		
		return columnList.toArray(new String[columnList.size()]);
	}

}
