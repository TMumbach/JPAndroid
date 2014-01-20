package com.jpandroid.core;

import com.jpandroid.annotations.Table;
import com.jpandroid.database.DatabaseHelper;
import com.jpandroid.entity.Entity;

/**
 * Adi��o de todas as entidades da aplica��o, 
 * as classes que aqui mapeadas, ter�o seu espelhamento no banco.
 * Somente as classes que possuem a anota��o {@link Table} ser�o
 * aceitas.
 * 
 * veja fun��o <code>createDataBase()</code> em {@link DatabaseHelper} 
 * 
 * @author tiago
 *
 */
@SuppressWarnings("all")
public class MappingEntities {
	
	/*
	 * ATEN��O: cuidado com a ordem das classes. Ex: se B depende A, ent�o A deve estar antes de B
	 * para que na cria��o da base, B possa encontrar a referencia ja criada de A. 
	 */
	private Class<? extends Entity>[] entities;
	
	public Class<? extends Entity>[] loadEntities() {
		return entities;
	}
	
	public void addAll(Class<? extends Entity>[] entities) {
		this.entities = entities;
	}
}
