package com.jpandroid.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NamedQuery 
{
	public abstract String name();
	
	public abstract String selection() default "";
	
	public abstract String[] columns() default {};
}
