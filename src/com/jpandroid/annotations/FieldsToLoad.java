package com.jpandroid.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Aten��o. Essa anota��o funcionara apenas se o <code>load</code> da anota��o
 * {@link ForeignKey} n�o for definido nenhum valor
 * 
 * @author tiago
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldsToLoad
{
	public String[] fields();

}
