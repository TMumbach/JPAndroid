package com.jpandroid.core;

import java.util.List;
import java.util.Map;

import com.jpandroid.criteria.Query;
import com.jpandroid.entity.Entity;
import com.jpandroid.types.LoadType;

@SuppressWarnings("all")
public interface EntityManager
{
	/**
	 * Insert or update if necessary
	 * 
	 * @param entity
	 * @return 
	 */
	public Entity insert(Entity entity);
	
	/**
	 * Update an entity
	 * 
	 * @param entity
	 * @return 
	 */
	public Entity update(Entity entity);

	/**
	 * Delete entity
	 * 
	 * @param entity
	 */
	public void delete(Entity entity);
	
	/**
	 * Delete entity by id
	 * 
	 * @param entity
	 */
	public void delete(Class<?> entity, Object id);

	/**
	 * Insert a list or update if necessary
	 * 
	 * @param entity
	 */
	public void insert(List<Entity> entity);

	/**
	 * Retrieve a list of entities ({@link Entity})
	 * 
	 * @param query
	 * @return
	 */
	public <T extends Entity> List<T> select(Class<T> returnType, Query query);
	
	
	/**
	 * Retrieve a list of entities ({@link Entity})
	 * 
	 * @param query
	 * @return
	 */
	public <T extends Entity> List<T> select(Class<T> returnType, String selection);
	
	/**
	 * Retrieve a list of entities ({@link Entity})
	 * 
	 * @param query
	 * @return
	 */
	public <T extends Entity> List<T> select(Class<T> returnType, String selection, String...columns);

	/**
	 * Retrieve a single {@link Entity} by id
	 * 
	 * @param entity
	 * @param id
	 * @return
	 */
	public <T extends Entity> T selectById(Class<T> entity, Object id);

	/**
	 * Retrieve a single {@link Entity}
	 * @param query
	 * @return
	 */
	public <T extends Entity> T selectSingle(Class<T> returnType, Query query);
	
	/**
	 * Retrieve a single {@link Entity}
	 * @param query
	 * @return
	 */
	public <T extends Entity> T selectSingle(Class<T> returnType, String selection);
	
	/**
	 * Retrieve a single {@link Entity}
	 * @param query
	 * @return
	 */
	public <T extends Entity> T selectSingle(Class<T> returnType, String selection, String...columns);
	
	/**
	 * If you are using {@link LoadType.ONLY_ID} you can use this method
	 * @param entity
	 * @return a populated entity
	 */
	public <T extends Entity> T loadEntity(Class<T> returnType, Entity entity);

	/**
	 * Get a count of a {@link Entity}
	 * 
	 * @param entity
	 * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause. 
	 * @return
	 */
	public <T> Integer count(Query query);

	/**
	 * Get count
	 * 
	 * @param type Return type
	 * @param query
	 * @return
	 */
	public <T> T max(Class<T> returnType, Query query);

	/**
	 * Just work for integer, double and float values
	 * 
	 * @param type Return type
	 * @param query
	 * @return
	 */
	public <T> T sum(Class<T> returnType, Query query);
	
	/**
	 * 
	 * @param type
	 * @param name
	 * @param parameters
	 * @return
	 */
	public <T extends Entity> List<T> getListByNamedQuery(Class<T> type, String name, Map<String, Object> parameters);
	
	/**
	 * 
	 * @param type
	 * @param name
	 * @param parameters
	 * @return
	 */
	public <T extends Entity> T getSingleByNamedQuery(Class<T> type, String name, Map<String, Object> parameters);

}
