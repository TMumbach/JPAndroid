package com.jpandroid.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jpandroid.types.ColumnType;
import com.jpandroid.types.LoadType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey
{
	public abstract String referencedColumnName();

	public abstract String name();

	public abstract LoadType load() default LoadType.ONLY_ID;

	public abstract ColumnType referencedColumnType() default ColumnType.NUMBER;

}
