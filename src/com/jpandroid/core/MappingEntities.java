package com.jpandroid.core;

import com.jpandroid.annotations.Table;
import com.jpandroid.database.DatabaseHelper;
import com.jpandroid.entity.Entity;

/**
 * Adição de todas as entidades da aplicação, 
 * as classes que aqui mapeadas, terão seu espelhamento no banco.
 * Somente as classes que possuem a anotação {@link Table} serão
 * aceitas.
 * 
 * veja função <code>createDataBase()</code> em {@link DatabaseHelper} 
 * 
 * @author tiago
 *
 */
@SuppressWarnings("all")
public class MappingEntities {
	
	/*
	 * ATENÇÂO: cuidado com a ordem das classes. Ex: se B depende A, então A deve estar antes de B
	 * para que na criação da base, B possa encontrar a referencia ja criada de A. 
	 */
	private Class<? extends Entity>[] entities;
	
	public Class<? extends Entity>[] loadEntities() {
		return entities;
	}
	
	public void addAll(Class<? extends Entity>[] entities) {
		this.entities = entities;
	}
}
