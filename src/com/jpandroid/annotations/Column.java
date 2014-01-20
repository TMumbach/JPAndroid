package com.jpandroid.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jpandroid.types.DateType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column
{
	public abstract String name() default "";

	public abstract int length() default -1;
	
	public abstract DateType dateType() default DateType.DATE;

}
